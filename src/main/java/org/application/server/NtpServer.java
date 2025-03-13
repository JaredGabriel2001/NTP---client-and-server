package org.application.server;

import org.application.encryption.HmacUtil;
import org.application.ntp.NtpPacket;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class NtpServer {
    private final boolean useHmac;
    private final int port;
    private static final String SECRET_KEY = "shared-secret";
    private DatagramSocket socket;
    private volatile boolean running;

    public NtpServer(boolean useHmac, int port) {
        this.useHmac = useHmac;
        this.port = port;
        this.running = true;
    }

    public void start() throws Exception {
        try (DatagramSocket serverSocket = new DatagramSocket(port)) {
            this.socket = serverSocket;
            System.out.println("[Servidor] Iniciado na porta " + port);

            while (running) {
                byte[] buffer = new byte[80];
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                System.out.println("[Servidor] Aguardando requisição...");
                socket.receive(request);
                System.out.println("[Servidor] Requisição recebida de " + request.getAddress());

                // Valida HMAC (se necessário)
                byte[] receivedData = Arrays.copyOfRange(request.getData(), 0, 48);
                if (useHmac) {
                    byte[] receivedHmac = Arrays.copyOfRange(request.getData(), 48, request.getLength());
                    byte[] calculatedHmac = HmacUtil.generateHmac(receivedData, SECRET_KEY);
                    if (!Arrays.equals(receivedHmac, calculatedHmac)) {
                        System.err.println("[Servidor] HMAC inválido! Rejeitando pacote.");
                        continue;
                    }
                }

                // Prepara a resposta
                NtpPacket responsePacket = new NtpPacket();
                responsePacket.setMode((byte) 4); // Modo servidor
                responsePacket.setStratum((byte) 1);
                responsePacket.setOriginateTimestamp(NtpPacket.fromByteArray(receivedData).getTransmitTimestamp());
                responsePacket.setReceiveTimestamp(System.currentTimeMillis());
                responsePacket.setTransmitTimestamp(System.currentTimeMillis());

                // Gera HMAC (se necessário)
                byte[] responseData = responsePacket.toByteArray();
                byte[] hmac = useHmac ? HmacUtil.generateHmac(responseData, SECRET_KEY) : new byte[0];
                byte[] dataToSend = ByteBuffer.allocate(responseData.length + hmac.length)
                        .put(responseData)
                        .put(hmac)
                        .array();

                // Envia a resposta
                DatagramPacket response = new DatagramPacket(
                        dataToSend,
                        dataToSend.length,
                        request.getAddress(),
                        request.getPort()
                );
                socket.send(response);
                System.out.println("[Servidor] Resposta enviada.");
            }
        } catch (Exception e) {
            System.err.println("[Servidor] Erro: " + e.getMessage());
        }
    }

    public void stop() {
        running = false;
        if (socket != null && !socket.isClosed()) {
            socket.close();
            System.out.println("[Servidor] Parado.");
        }
    }
}