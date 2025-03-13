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
            socket.setSoTimeout(10000); // Timeout de 10 segundos
            InetAddress address = InetAddress.getByName(serverAddress);

            // Cria o pacote de requisição
            NtpPacket requestPacket = new NtpPacket();
            requestPacket.setMode((byte) 3); // Modo cliente
            long t1 = toNtpTimestamp(System.currentTimeMillis());
            requestPacket.setTransmitTimestamp(t1);

            // Gera HMAC (se necessário)
            byte[] packetData = requestPacket.toByteArray();
            byte[] hmac = useHmac ? HmacUtil.generateHmac(packetData, SECRET_KEY) : new byte[0];
            byte[] dataToSend = ByteBuffer.allocate(packetData.length + hmac.length)
                    .put(packetData)
                    .put(hmac)
                    .array();

            // Envia a requisição
            DatagramPacket request = new DatagramPacket(dataToSend, dataToSend.length, address, port);
            System.out.println("[Cliente] Enviando requisição para " + serverAddress + ":" + port);
            socket.send(request);
            System.out.println("[Cliente] Requisição enviada. Aguardando resposta...");

            // Recebe a resposta
            byte[] buffer = new byte[80];
            DatagramPacket response = new DatagramPacket(buffer, buffer.length);
            socket.receive(response);
            System.out.println("[Cliente] Resposta recebida!");

            // Valida HMAC (se necessário)
            byte[] receivedData = Arrays.copyOfRange(buffer, 0, 48);
            if (useHmac) {
                byte[] receivedHmac = Arrays.copyOfRange(buffer, 48, buffer.length);
                byte[] calculatedHmac = HmacUtil.generateHmac(receivedData, SECRET_KEY);
                if (!Arrays.equals(receivedHmac, calculatedHmac)) {
                    throw new SecurityException("HMAC inválido na resposta!");
                }
            }

            // Processa a resposta
            NtpPacket responsePacket = NtpPacket.fromByteArray(receivedData);
            long t2 = responsePacket.getReceiveTimestamp();
            long t3 = responsePacket.getTransmitTimestamp();
            long t4 = toNtpTimestamp(System.currentTimeMillis());

            // Converte timestamps para milissegundos
            long t1Millis = fromNtpTimestamp(t1);
            long t2Millis = fromNtpTimestamp(t2);
            long t3Millis = fromNtpTimestamp(t3);
            long t4Millis = fromNtpTimestamp(t4);

            // Exibe os resultados
            System.out.println("T1 (Cliente): " + t1Millis);
            System.out.println("T2 (Servidor): " + t2Millis);
            System.out.println("T3 (Servidor): " + t3Millis);
            System.out.println("T4 (Cliente): " + t4Millis);
        } catch (Exception e) {
            System.err.println("[Cliente] Erro: " + e.getMessage());
            throw e; // Propaga a exceção para o teste falhar
        }
    }

    // Métodos de conversão de timestamp
    public static long toNtpTimestamp(long millis) {
        long seconds = (millis / 1000) + 2208988800L;
        long fraction = ((millis % 1000) * 0x100000000L) / 1000;
        return (seconds << 32) | fraction;
    }

    public static long fromNtpTimestamp(long ntpTimestamp) {
        long seconds = (ntpTimestamp >>> 32) - 2208988800L;
        long fraction = (ntpTimestamp & 0xFFFFFFFFL) * 1000 / 0x100000000L;
        return (seconds * 1000) + fraction;
    }
}