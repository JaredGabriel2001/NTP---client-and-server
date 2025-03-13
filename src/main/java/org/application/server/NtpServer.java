package org.application.server;

import org.application.encryption.HmacUtil;
import org.application.ntp.NtpPacket;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class NtpServer {
    // Flag para indicar se o servidor deve usar HMAC para autenticação.
    private final boolean useHmac;
    // Porta na qual o servidor irá escutar.
    private final int port;
    // Chave secreta compartilhada para geração e verificação de HMAC.
    private static final String SECRET_KEY = "shared-secret";
    // Socket UDP usado para comunicação.
    private DatagramSocket socket;
    // Flag para controlar o loop principal do servidor.
    private volatile boolean running;

    // Construtor da classe NtpServer.
    public NtpServer(boolean useHmac, int port) {
        this.useHmac = useHmac;
        this.port = port;
        this.running = true;
    }

    // Método para iniciar o servidor.
    public void start() throws Exception {
        // Bloco try-with-resources para garantir que o socket seja fechado automaticamente.
        try (DatagramSocket serverSocket = new DatagramSocket(port)) {
            // Armazena o socket criado na variável de instância.
            this.socket = serverSocket;
            // Imprime uma mensagem informando que o servidor foi iniciado.
            System.out.println("[Servidor] Iniciado na porta " + port);

            // Loop principal do servidor.
            while (running) {
                // Buffer para receber os dados do pacote UDP.
                byte[] buffer = new byte[80];
                // Cria um DatagramPacket para receber a requisição.
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                // Imprime uma mensagem informando que o servidor está aguardando uma requisição.
                System.out.println("[Servidor] Aguardando requisição...");
                // Recebe o pacote UDP.
                socket.receive(request);
                // Imprime uma mensagem informando que uma requisição foi recebida.
                System.out.println("[Servidor] Requisição recebida de " + request.getAddress());

                // Validação do HMAC (se habilitado).
                // Extrai os dados recebidos (excluindo o HMAC, se presente).
                byte[] receivedData = Arrays.copyOfRange(request.getData(), 0, 48);
                if (useHmac) {
                    // Extrai o HMAC recebido do pacote.
                    byte[] receivedHmac = Arrays.copyOfRange(request.getData(), 48, request.getLength());
                    // Calcula o HMAC esperado usando a chave secreta.
                    byte[] calculatedHmac = HmacUtil.generateHmac(receivedData, SECRET_KEY);
                    // Compara o HMAC recebido com o calculado.
                    if (!Arrays.equals(receivedHmac, calculatedHmac)) {
                        // Se os HMACs não coincidirem, imprime um erro e descarta o pacote.
                        System.err.println("[Servidor] HMAC inválido! Rejeitando pacote.");
                        continue;
                    }
                }

                // Preparação da resposta.
                // Cria um objeto NtpPacket para a resposta.
                NtpPacket responsePacket = new NtpPacket();
                // Define o modo do pacote como servidor (4).
                responsePacket.setMode((byte) 4);
                // Define o stratum como 1 (servidor primário).
                responsePacket.setStratum((byte) 1);
                // Define o timestamp de origem como o timestamp de transmissão da requisição.
                responsePacket.setOriginateTimestamp(NtpPacket.fromByteArray(receivedData).getTransmitTimestamp());
                // Define o timestamp de recebimento como o tempo atual.
                responsePacket.setReceiveTimestamp(System.currentTimeMillis());
                // Define o timestamp de transmissão como o tempo atual.
                responsePacket.setTransmitTimestamp(System.currentTimeMillis());

                // Geração do HMAC para a resposta (se habilitado).
                // Converte o objeto NtpPacket em um array de bytes.
                byte[] responseData = responsePacket.toByteArray();
                // Gera o HMAC para a resposta, se necessário.
                byte[] hmac = useHmac ? HmacUtil.generateHmac(responseData, SECRET_KEY) : new byte[0];
                // Combina os dados da resposta e o HMAC em um único array de bytes.
                byte[] dataToSend = ByteBuffer.allocate(responseData.length + hmac.length)
                        .put(responseData)
                        .put(hmac)
                        .array();

                // Envio da resposta.
                // Cria um DatagramPacket para a resposta.
                DatagramPacket response = new DatagramPacket(
                        dataToSend,
                        dataToSend.length,
                        request.getAddress(),
                        request.getPort()
                );
                // Envia a resposta.
                socket.send(response);
                // Imprime uma mensagem informando que a resposta foi enviada.
                System.out.println("[Servidor] Resposta enviada.");
            }
        } catch (Exception e) {
            // Captura e imprime erros que ocorram durante a execução do servidor.
            System.err.println("[Servidor] Erro: " + e.getMessage());
        }
    }

    // Método para parar o servidor.
    public void stop() {
        // Define a flag running como false para interromper o loop principal.
        running = false;
        // Verifica se o socket está aberto e o fecha.
        if (socket != null && !socket.isClosed()) {
            socket.close();
            // Imprime uma mensagem informando que o servidor foi parado.
            System.out.println("[Servidor] Parado.");
        }
    }
}