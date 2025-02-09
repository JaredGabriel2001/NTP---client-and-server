package org.server;

import org.encryption.HmacUtil;
import org.ntp.NtpPacket;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class NtpServer {
    private static final String SECRET_KEY = "shared-secret";
    private final boolean useHmac;
    private final int port;

    public NtpServer(boolean useHmac, int port) {
        this.useHmac = useHmac;
        this.port = port;
    }

    public void start() throws Exception {
        DatagramSocket socket = new DatagramSocket(port);
        System.out.println("NTP Server started on port " + port + " using " + (useHmac ? "HMAC" : "plain NTP"));

        while (true) {
            int packetSize = useHmac ? 80 : 48;
            byte[] buffer = new byte[packetSize];
            DatagramPacket request = new DatagramPacket(buffer, buffer.length);
            socket.receive(request);

            NtpPacket packet = new NtpPacket();
            if (useHmac) {
                byte[] receivedData = Arrays.copyOfRange(buffer, 0, 48);
                byte[] receivedHmac = Arrays.copyOfRange(buffer, 48, packetSize);
                byte[] calculatedHmac = HmacUtil.generateHmac(receivedData, SECRET_KEY);
                if (!Arrays.equals(receivedHmac, calculatedHmac)) {
                    System.err.println("Invalid HMAC received. Dropping packet.");
                    continue;
                }
                System.arraycopy(receivedData, 0, packet.toByteArray(), 0, 48);
            } else {
                System.arraycopy(buffer, 0, packet.toByteArray(), 0, 48);
            }

            //Define o timestamp de transmissão com a hora atual do servidor
            packet.setTransmitTimestamp(System.currentTimeMillis());

            byte[] dataToSend;
            if (useHmac) {
                byte[] responseData = packet.toByteArray();
                byte[] hmac = HmacUtil.generateHmac(responseData, SECRET_KEY);
                dataToSend = ByteBuffer.allocate(responseData.length + hmac.length)
                        .put(responseData)
                        .put(hmac)
                        .array();
            } else {
                dataToSend = packet.toByteArray();
            }

            DatagramPacket response = new DatagramPacket(dataToSend, dataToSend.length, request.getAddress(), request.getPort());
            socket.send(response);
        }
    }
}
