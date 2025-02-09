package org.application.server;

import org.application.ntp.NtpPacket;
import org.application.encryption.HmacUtil;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class NtpServer {
    private final boolean useHmac;
    private final int port;
    private static final String SECRET_KEY = "shared-secret";

    public NtpServer(boolean useHmac, int port) {
        this.useHmac = useHmac;
        this.port = port;
    }

    public void start() throws Exception {
        try (DatagramSocket socket = new DatagramSocket(port)) {
            System.out.println("NTP Server started on port " + port);
            while (true) {
                byte[] buffer = new byte[64];
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                socket.receive(request);

                byte[] receivedData = Arrays.copyOfRange(buffer, 0, 48);
                if (useHmac) {
                    byte[] receivedHmac = Arrays.copyOfRange(buffer, 48, buffer.length);
                    byte[] calculatedHmac = HmacUtil.generateHmac(receivedData, SECRET_KEY);
                    if (!Arrays.equals(receivedHmac, calculatedHmac)) {
                        System.err.println("Invalid HMAC received. Dropping packet.");
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
                responsePacket.setMode((byte) 4);       // Modo servidor
                responsePacket.setStratum((byte) 1);      // Stratum 1 (servidor primário)
                responsePacket.setOriginateTimestamp(t1); // Copia T1 para o campo Originate
                responsePacket.setReceiveTimestamp(t2);   // Define T2
                responsePacket.setReferenceTimestamp(t2); // Para simplicidade, usamos t2
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
            }
        }
    }
}
