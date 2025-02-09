package org.client;

import org.encryption.HmacUtil;
import org.ntp.NtpPacket;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Date;

public class NtpClient {
    private static final String SECRET_KEY = "shared-secret";
    private final boolean useHmac;
    private final int port; // 8123 para custom, 123 para oficial

    public NtpClient(boolean useHmac, int port) {
        this.useHmac = useHmac;
        this.port = port;
    }

    public void requestTime(String serverAddress) throws Exception {
        DatagramSocket socket = new DatagramSocket();
        socket.setSoTimeout(5000);
        InetAddress address = InetAddress.getByName(serverAddress);
        NtpPacket packet = new NtpPacket();
        packet.setTransmitTimestamp(System.currentTimeMillis());

        byte[] dataToSend;
        if (useHmac) {
            byte[] data = packet.toByteArray();
            byte[] hmac = HmacUtil.generateHmac(data, SECRET_KEY);
            dataToSend = ByteBuffer.allocate(data.length + hmac.length)
                    .put(data)
                    .put(hmac)
                    .array();
        } else {
            dataToSend = packet.toByteArray();
        }

        DatagramPacket request = new DatagramPacket(dataToSend, dataToSend.length, address, port);
        System.out.println("Enviando requisição NTP para " + serverAddress + ":" + port);
        socket.send(request);

        int expectedResponseSize = useHmac ? 80 : 48;
        byte[] buffer = new byte[expectedResponseSize];
        DatagramPacket response = new DatagramPacket(buffer, buffer.length);

        try {
            socket.receive(response);
            System.out.println("Resposta recebida com tamanho: " + response.getLength() + " bytes");
        } catch (java.net.SocketTimeoutException ste) {
            System.err.println("Timeout: Nenhuma resposta recebida em 5 segundos.");
            socket.close();
            return;
        }

        if (response.getLength() < 48) {
            System.err.println("Pacote recebido é muito curto (" + response.getLength() + " bytes).");
            socket.close();
            return;
        }

        if (useHmac) {
            if (response.getLength() != expectedResponseSize) {
                System.err.println("Tamanho da resposta inesperado. Esperado " + expectedResponseSize +
                        " bytes, mas recebido " + response.getLength() + " bytes.");
                socket.close();
                return;
            }
            byte[] receivedData = Arrays.copyOfRange(buffer, 0, 48);
            byte[] receivedHmac = Arrays.copyOfRange(buffer, 48, expectedResponseSize);
            byte[] calculatedHmac = HmacUtil.generateHmac(receivedData, SECRET_KEY);
            if (!Arrays.equals(receivedHmac, calculatedHmac)) {
                socket.close();
                throw new SecurityException("Falha na autenticação: HMAC inválido!");
            }
            NtpPacket receivedPacket = new NtpPacket();
            System.arraycopy(receivedData, 0, receivedPacket.toByteArray(), 0, 48);
            long serverTime = receivedPacket.getServerTime();
            Date serverDate = new Date(serverTime);
            System.out.println("Hora do Servidor: " + serverDate.toString());
        } else {
            NtpPacket receivedPacket = new NtpPacket();
            System.arraycopy(buffer, 0, receivedPacket.toByteArray(), 0, 48);
            long serverTime = receivedPacket.getServerTime();
            Date serverDate = new Date(serverTime);
            System.out.println("Hora do Servidor: " + serverDate.toString());
        }
        socket.close();
    }
}
