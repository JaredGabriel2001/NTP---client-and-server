package org.example.server;

import org.example.encryption.HmacUtil;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;
import java.util.Arrays;
import org.example.ntp.NtpPacket;

public class NtpServer {
    private static final int NTP_PORT = 8123;
    private static final String SECRET_KEY = "shared-secret";

    public void start() throws Exception {
        DatagramSocket socket = new DatagramSocket(NTP_PORT);
        System.out.println("NTP Server started on port " + NTP_PORT);

        while (true) {
            byte[] buffer = new byte[64];
            DatagramPacket request = new DatagramPacket(buffer, buffer.length);
            socket.receive(request);

            byte[] receivedHmac = Arrays.copyOfRange(buffer, 48, buffer.length);
            byte[] receivedData = Arrays.copyOf(buffer, 48);

            byte[] calculatedHmac = HmacUtil.generateHmac(receivedData, SECRET_KEY);
            if (!Arrays.equals(receivedHmac, calculatedHmac)) {
                System.err.println("Invalid HMAC received. Dropping packet.");
                continue;
            }

            NtpPacket packet = new NtpPacket();
            System.arraycopy(receivedData, 0, packet.toByteArray(), 0, 48);

            long receiveTime = System.currentTimeMillis();
            packet.setTransmitTimestamp(receiveTime);

            byte[] hmac = HmacUtil.generateHmac(packet.toByteArray(), SECRET_KEY);
            byte[] dataToSend = ByteBuffer.allocate(packet.toByteArray().length + hmac.length)
                    .put(packet.toByteArray())
                    .put(hmac)
                    .array();

            DatagramPacket response = new DatagramPacket(dataToSend, dataToSend.length, request.getAddress(), request.getPort());
            socket.send(response);
        }
    }
}