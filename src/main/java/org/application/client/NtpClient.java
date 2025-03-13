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
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setSoTimeout(5000);
            InetAddress address = InetAddress.getByName(serverAddress);

            // Cria o pacote de requisição
            NtpPacket requestPacket = new NtpPacket();
            requestPacket.setMode((byte) 3); // Modo cliente

            // T1: Tempo de envio do cliente
            long t1 = NtpClient.toNtpTimestamp(System.currentTimeMillis());
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

            // T4: Tempo de chegada da resposta no cliente
            long t4 = NtpClient.toNtpTimestamp(System.currentTimeMillis());

            byte[] receivedData = Arrays.copyOfRange(buffer, 0, 48);
            if (useHmac) {
                byte[] receivedHmac = Arrays.copyOfRange(buffer, 48, buffer.length);
                byte[] calculatedHmac = HmacUtil.generateHmac(receivedData, SECRET_KEY);
                if (!Arrays.equals(receivedHmac, calculatedHmac)) {
                    throw new SecurityException("Authentication failed: Invalid HMAC!");
                }
            }

            NtpPacket responsePacket = NtpPacket.fromByteArray(receivedData);
            long originate = responsePacket.getOriginateTimestamp(); // T1
            long t2 = responsePacket.getReceiveTimestamp(); // T2
            long t3 = responsePacket.getTransmitTimestamp(); // T3

            // Converte os timestamps para milissegundos do sistema
            long t1Millis = NtpClient.fromNtpTimestamp(originate);
            long t2Millis = NtpClient.fromNtpTimestamp(t2);
            long t3Millis = NtpClient.fromNtpTimestamp(t3);
            long t4Millis = NtpClient.fromNtpTimestamp(t4);

            // Cálculo do delay e do offset
            long roundTripDelay = (t4Millis - t1Millis) - (t3Millis - t2Millis);
            long offset = ((t2Millis - t1Millis) + (t3Millis - t4Millis)) / 2;

            System.out.println("Client Send Time (T1): " + t1Millis);
            System.out.println("Server Receive Time (T2): " + t2Millis);
            System.out.println("Server Transmit Time (T3): " + t3Millis);
            System.out.println("Client Receive Time (T4): " + t4Millis);
            System.out.println("Round Trip Delay: " + roundTripDelay + " ms");
            System.out.println("Clock Offset: " + offset + " ms");
            System.out.println("Adjusted Server Time: " + (t4Millis + offset));
        }
    }

    /**
     * Converte um timestamp em milissegundos para o formato NTP (64 bits).
     */
    public static long toNtpTimestamp(long currentTimeMillis) {
        long seconds = (currentTimeMillis / 1000) + 2208988800L; // Ajustar para a época NTP
        long fraction = ((currentTimeMillis % 1000) * 4294967296L) / 1000; // (2^32 / 1000)
        return (seconds << 32) | (fraction & 0xFFFFFFFFL);
    }

    /**
     * Converte um timestamp no formato NTP (64 bits) para milissegundos do sistema.
     */
    public static long fromNtpTimestamp(long ntpTimestamp) {
        long seconds = (ntpTimestamp >>> 32) - 2208988800L; // Retorna à época Unix
        long fraction = (ntpTimestamp & 0xFFFFFFFFL) * 1000 / 4294967296L; // Converter fração para ms
        return (seconds * 1000) + fraction;
    }
}