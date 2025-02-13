package org.application.client;

import org.application.encryption.HmacUtil;
import org.application.ntp.NtpPacket;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class NtpClient {
    private final boolean useHmac;
    private final int port;
    private static final String SECRET_KEY = "shared-secret";

    public NtpClient(boolean useHmac, int port) {
        this.useHmac = useHmac;
        this.port = port;
    }

    public void requestTime(String serverAddress) throws Exception {
        // Utiliza try-with-resources para garantir que o socket seja fechado.
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setSoTimeout(5000);
            InetAddress address = InetAddress.getByName(serverAddress);

            // Cria o pacote de requisição
            NtpPacket requestPacket = new NtpPacket();
            requestPacket.setMode((byte) 3); // Modo cliente
            // T1: tempo de envio do cliente
            long t1 = System.currentTimeMillis();
            requestPacket.setTransmitTimestamp(t1);

            byte[] packetData = requestPacket.toByteArray();
            byte[] hmac = useHmac ? HmacUtil.generateHmac(packetData, SECRET_KEY) : new byte[0];
            byte[] dataToSend = ByteBuffer.allocate(packetData.length + hmac.length)
                    .put(packetData)
                    .put(hmac)
                    .array();

            DatagramPacket request = new DatagramPacket(dataToSend, dataToSend.length, address, port);
            socket.send(request);

            // Prepara o buffer para a resposta
            byte[] buffer = new byte[80];
            DatagramPacket response = new DatagramPacket(buffer, buffer.length);
            socket.receive(response);
            // T4: tempo de chegada da resposta no cliente
            long t4 = System.currentTimeMillis();

            byte[] receivedData = Arrays.copyOfRange(buffer, 0, 48);
            if (useHmac) {
                byte[] receivedHmac = Arrays.copyOfRange(buffer, 48, buffer.length);
                byte[] calculatedHmac = HmacUtil.generateHmac(receivedData, SECRET_KEY);
                if (!Arrays.equals(receivedHmac, calculatedHmac)) {
                    throw new SecurityException("Authentication failed: Invalid HMAC!");
                }
            }

            NtpPacket responsePacket = NtpPacket.fromByteArray(receivedData);
            // Os timestamps do pacote de resposta:
            // T1 (Originate Timestamp) – cópia do tempo enviado pelo cliente;
            // T2 (Receive Timestamp) – tempo em que o servidor recebeu a requisição;
            // T3 (Transmit Timestamp) – tempo em que o servidor enviou a resposta.
            long originate = responsePacket.getOriginateTimestamp(); // Deve ser igual a t1
            long t2 = responsePacket.getReceiveTimestamp();
            long t3 = responsePacket.getTransmitTimestamp();

            // Cálculo do delay e do offset
            long roundTripDelay = (t4 - t1) - (t3 - t2);
            long offset = ((t2 - t1) + (t3 - t4)) / 2;

            System.out.println("Client Send Time (T1): " + t1);
            System.out.println("Server Receive Time (T2): " + t2);
            System.out.println("Server Transmit Time (T3): " + t3);
            System.out.println("Client Receive Time (T4): " + t4);
            System.out.println("Round Trip Delay: " + roundTripDelay + " ms");
            System.out.println("Clock Offset: " + offset + " ms");
            System.out.println("Adjusted Server Time: " + (t4 + offset));
        }
    }
}
