package org.application.server;

import org.application.encryption.HmacUtil;
import org.application.ntp.NtpPacket;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class NtpServer {
    private static final String SECRET_KEY = "shared-secret";
    private final boolean useHmac;
    private final int port;
    private DatagramSocket socket;

    public NtpServer(boolean useHmac, int port) {
        this.useHmac = useHmac;
        this.port = port;
    }

    public void start() throws Exception {
        socket = new DatagramSocket(port);
        System.out.println("Servidor NTP iniciado na porta " + port);

        while (true) {
            byte[] buffer = new byte[80];
            DatagramPacket request = new DatagramPacket(buffer, buffer.length);
            System.out.println("Aguardando requisição...");
            socket.receive(request);
            System.out.println("Requisição recebida de " + request.getAddress() + ":" + request.getPort());

            byte[] receivedData = Arrays.copyOfRange(buffer, 0, 48);
            if (useHmac) {
                byte[] receivedHmac = Arrays.copyOfRange(buffer, 48, buffer.length);
                byte[] calculatedHmac = HmacUtil.generateHmac(receivedData, SECRET_KEY);
                if (!Arrays.equals(receivedHmac, calculatedHmac)) {
                    System.err.println("HMAC inválido! Rejeitando requisição.");
                    continue;
                }
            }

            // Interpreta o pacote recebido
            NtpPacket requestPacket = NtpPacket.fromByteArray(receivedData);
            // T1: timestamp do cliente (campo Transmit do pedido)
            long t1 = requestPacket.getTransmitTimestamp();
            // T2: tempo de recepção no servidor
            long t2 = System.currentTimeMillis();

            // Cria o pacote de resposta
            NtpPacket responsePacket = new NtpPacket();
            responsePacket.setMode((byte) 4);         // Modo servidor
            responsePacket.setStratum((byte) 1);      // Stratum 1 (servidor primário)
            responsePacket.setOriginateTimestamp(t1); // Copia T1 para o campo Originate
            responsePacket.setReceiveTimestamp(t2);   // Define T2
            responsePacket.setReferenceTimestamp(t2); // Para simplicidade, usamos t2
            responsePacket.setRootDelay(0);           // Root Delay (0 para servidor primário)
            responsePacket.setRootDispersion(0);      // Root Dispersion (0 para servidor primário)
            responsePacket.setReferenceIdentifier(0); // Reference Identifier (0 para servidor primário)
            long t3 = System.currentTimeMillis();     // T3: tempo de envio da resposta
            responsePacket.setTransmitTimestamp(t3);

            byte[] responseData = responsePacket.toByteArray();
            byte[] hmac = useHmac ? HmacUtil.generateHmac(responseData, SECRET_KEY) : new byte[0];
            byte[] dataToSend = ByteBuffer.allocate(responseData.length + hmac.length)
                    .put(responseData)
                    .put(hmac)
                    .array();

            DatagramPacket response = new DatagramPacket(
                    dataToSend,
                    dataToSend.length,
                    request.getAddress(),
                    request.getPort()
            );
            socket.send(response);
            System.out.println("Resposta enviada para o cliente.");
        }
    }

    public void stop() {
        if (socket != null && !socket.isClosed()) {
            socket.close(); // Fecha o socket para liberar a porta
            System.out.println("Servidor NTP parado.");
        }
    }
}