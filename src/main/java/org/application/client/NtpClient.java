package org.application.client;

import org.application.encryption.HmacUtil;
import org.application.ntp.NtpPacket;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class NtpClient {
    private final boolean useHmac; // Define se o cliente usará HMAC para autenticação
    private final int port; // Porta do servidor NTP
    private static final String SECRET_KEY = "shared-secret"; // Chave secreta para HMAC

    // Construtor do cliente NTP, recebe um booleano indicando o uso de HMAC e a porta do servidor
    public NtpClient(boolean useHmac, int port) {
        this.useHmac = useHmac;
        this.port = port;
    }

    // Método que envia uma requisição NTP e processa a resposta
    public void requestTime(String serverAddress) throws Exception {
        try (DatagramSocket socket = new DatagramSocket()) { // Cria um socket UDP
            socket.setSoTimeout(10000); // Define um timeout de 10 segundos para evitar bloqueio
            InetAddress address = InetAddress.getByName(serverAddress); // Resolve o endereço do servidor

            // Cria o pacote de requisição NTP
            NtpPacket requestPacket = new NtpPacket();
            requestPacket.setMode((byte) 3); // Define o modo cliente (3)
            long t1 = toNtpTimestamp(System.currentTimeMillis()); // Obtém o timestamp atual e converte para formato NTP
            requestPacket.setTransmitTimestamp(t1); // Define o timestamp de transmissão (T1)

            // Serializa o pacote para bytes
            byte[] packetData = requestPacket.toByteArray();
            // Gera o HMAC se necessário
            byte[] hmac = useHmac ? HmacUtil.generateHmac(packetData, SECRET_KEY) : new byte[0];
            // Concatena o pacote NTP com o HMAC (se usado)
            byte[] dataToSend = ByteBuffer.allocate(packetData.length + hmac.length)
                    .put(packetData)
                    .put(hmac)
                    .array();

            // Envia a requisição para o servidor NTP
            DatagramPacket request = new DatagramPacket(dataToSend, dataToSend.length, address, port);
            System.out.println("[Cliente] Enviando requisição para " + serverAddress + ":" + port);
            socket.send(request);
            System.out.println("[Cliente] Requisição enviada. Aguardando resposta...");

            // Cria um buffer para receber a resposta do servidor
            byte[] buffer = new byte[80];
            DatagramPacket response = new DatagramPacket(buffer, buffer.length);
            socket.receive(response); // Aguarda a resposta
            System.out.println("[Cliente] Resposta recebida!");

            // Se HMAC estiver ativado, valida a integridade da resposta
            byte[] receivedData = Arrays.copyOfRange(buffer, 0, 48); // Extrai o pacote NTP (48 bytes)
            if (useHmac) {
                byte[] receivedHmac = Arrays.copyOfRange(buffer, 48, buffer.length); // Extrai o HMAC recebido
                byte[] calculatedHmac = HmacUtil.generateHmac(receivedData, SECRET_KEY); // Calcula o HMAC esperado
                if (!Arrays.equals(receivedHmac, calculatedHmac)) {
                    throw new SecurityException("HMAC inválido na resposta!"); // Lança exceção se os HMACs não coincidirem
                }
            }

            // Converte os bytes da resposta para um objeto NtpPacket
            NtpPacket responsePacket = NtpPacket.fromByteArray(receivedData);
            long t2 = responsePacket.getReceiveTimestamp(); // Timestamp de recebimento do servidor
            long t3 = responsePacket.getTransmitTimestamp(); // Timestamp de transmissão do servidor
            long t4 = toNtpTimestamp(System.currentTimeMillis()); // Timestamp de chegada no cliente

            // Converte os timestamps de formato NTP para milissegundos
            long t1Millis = fromNtpTimestamp(t1);
            long t2Millis = fromNtpTimestamp(t2);
            long t3Millis = fromNtpTimestamp(t3);
            long t4Millis = fromNtpTimestamp(t4);

            // Exibe os timestamps obtidos
            System.out.println("T1 (Cliente): " + t1Millis);
            System.out.println("T2 (Servidor): " + t2Millis);
            System.out.println("T3 (Servidor): " + t3Millis);
            System.out.println("T4 (Cliente): " + t4Millis);
        } catch (Exception e) {
            System.err.println("[Cliente] Erro: " + e.getMessage());
            throw e; // Propaga a exceção para que o teste falhe
        }
    }

    // Converte um timestamp de milissegundos para o formato NTP
    public static long toNtpTimestamp(long millis) {
        long seconds = (millis / 1000) + 2208988800L; // Ajusta para a época NTP
        long fraction = ((millis % 1000) * 0x100000000L) / 1000; // Converte a fração de segundos
        return (seconds << 32) | fraction; // Junta segundos e fração em um único valor de 64 bits
    }

    // Converte um timestamp do formato NTP para milissegundos
    public static long fromNtpTimestamp(long ntpTimestamp) {
        long seconds = (ntpTimestamp >>> 32) - 2208988800L; // Recupera a parte inteira do tempo
        long fraction = (ntpTimestamp & 0xFFFFFFFFL) * 1000 / 0x100000000L; // Recupera a fração de segundos
        return (seconds * 1000) + fraction; // Retorna o tempo em milissegundos
    }
}