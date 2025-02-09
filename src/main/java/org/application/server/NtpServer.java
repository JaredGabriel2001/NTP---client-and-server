package org.application.server;

import org.application.ntp.NtpPacket;
import org.application.encryption.HmacUtil;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class NtpServer {
    private static final int NTP_PORT = 8123;
    private static final String SECRET_KEY = "shared-secret";
    private final boolean useHmac;
    private final int port;

    public NtpServer(boolean useHmac, int port) {
        this.useHmac = useHmac;
        this.port = port;
    }

    public void start() throws Exception {
        DatagramSocket socket = new DatagramSocket(port);
        System.out.println("NTP Server started on port " + port);

        while (true) {
            byte[] buffer = new byte[64];
            DatagramPacket request = new DatagramPacket(buffer, buffer.length);
            socket.receive(request);

            if (useHmac) {
                byte[] receivedHmac = Arrays.copyOfRange(buffer, 48, buffer.length);
                byte[] receivedData = Arrays.copyOf(buffer, 48);

                byte[] calculatedHmac = HmacUtil.generateHmac(receivedData, SECRET_KEY);
                if (!Arrays.equals(receivedHmac, calculatedHmac)) {
                    System.err.println("Invalid HMAC received. Dropping packet.");
                    continue;
                }
            }

            NtpPacket packet = new NtpPacket();
            System.arraycopy(buffer, 0, packet.toByteArray(), 0, 48);

            long receiveTime = System.currentTimeMillis();
            packet.setTransmitTimestamp(receiveTime);

            byte[] hmac = useHmac ? HmacUtil.generateHmac(packet.toByteArray(), SECRET_KEY) : new byte[0];
            byte[] dataToSend = ByteBuffer.allocate(packet.toByteArray().length + hmac.length)
                    .put(packet.toByteArray())
                    .put(hmac)
                    .array();

            DatagramPacket response = new DatagramPacket(dataToSend, dataToSend.length, request.getAddress(), request.getPort());
            socket.send(response);
        }
    }
}
