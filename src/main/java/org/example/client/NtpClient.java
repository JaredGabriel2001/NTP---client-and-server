package org.example.client;

import org.example.encryption.HmacUtil;
import org.example.ntp.NtpPacket;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class NtpClient {
    private static final String SECRET_KEY = "shared-secret";

    public void requestTime(String serverAddress, int port) throws Exception {
        DatagramSocket socket = new DatagramSocket();
        socket.setSoTimeout(5000);

        InetAddress address = InetAddress.getByName(serverAddress);
        NtpPacket packet = new NtpPacket();
        packet.setTransmitTimestamp(System.currentTimeMillis());

        byte[] hmac = HmacUtil.generateHmac(packet.toByteArray(), SECRET_KEY);
        byte[] dataToSend = ByteBuffer.allocate(packet.toByteArray().length + hmac.length)
                .put(packet.toByteArray())
                .put(hmac)
                .array();

        DatagramPacket request = new DatagramPacket(dataToSend, dataToSend.length, address, port);
        socket.send(request);

        byte[] buffer = new byte[64];
        DatagramPacket response = new DatagramPacket(buffer, buffer.length);
        socket.receive(response);

        byte[] receivedHmac = Arrays.copyOfRange(buffer, 48, buffer.length);
        byte[] receivedData = Arrays.copyOf(buffer, 48);

        byte[] calculatedHmac = HmacUtil.generateHmac(receivedData, SECRET_KEY);
        if (!Arrays.equals(receivedHmac, calculatedHmac)) {
            throw new SecurityException("Authentication failed: Invalid HMAC!");
        }

        NtpPacket receivedPacket = new NtpPacket();
        System.arraycopy(receivedData, 0, receivedPacket.toByteArray(), 0, 48);
        System.out.println("Server Time: " + receivedPacket.getReceiveTimestamp());
    }
}