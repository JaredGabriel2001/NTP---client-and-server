package org.application.ntp;

import java.nio.ByteBuffer;

public class NtpPacket {
    private static final int PACKET_SIZE = 48; // Tamanho do pacote NTP em bytes
    private static final long NTP_EPOCH = 2208988800L; // Diferença em segundos entre 1900 e 1970
    private final byte[] data; // Array de bytes representando o pacote NTP

    public NtpPacket() {
        data = new byte[PACKET_SIZE];
        data[0] = 0b00100011; // Configura LI=0, VN=4 e Mode=3 (cliente) por padrão.
    }

    // Retorna o array interno representando o pacote NTP.
    public byte[] toByteArray() {
        return data;
    }

    // Cria um objeto NtpPacket a partir de um array de bytes.
    public static NtpPacket fromByteArray(byte[] array) {
        if (array == null || array.length < PACKET_SIZE) {
            throw new IllegalArgumentException("Array must have at least " + PACKET_SIZE + " bytes.");
        }
        NtpPacket packet = new NtpPacket();
        System.arraycopy(array, 0, packet.data, 0, PACKET_SIZE);
        return packet;
    }

    // Define um timestamp (64 bits: 32 bits de segundos e 32 bits de fração) em um offset específico.
    private void setTimestamp(long millis, int offset) {
        long seconds = millis / 1000L + NTP_EPOCH; // Converte milissegundos para segundos no formato NTP
        long fraction = ((millis % 1000L) * 0x100000000L) / 1000L; // Calcula a fração de segundo
        ByteBuffer buffer = ByteBuffer.wrap(data);
        buffer.putInt(offset, (int) seconds); // Armazena os segundos no buffer
        buffer.putInt(offset + 4, (int) fraction); // Armazena a fração no buffer
    }

    // Extrai um timestamp (convertido para milissegundos) de um offset específico.
    private long getTimestamp(int offset) {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        long seconds = Integer.toUnsignedLong(buffer.getInt(offset)); // Recupera os segundos
        long fraction = Integer.toUnsignedLong(buffer.getInt(offset + 4)); // Recupera a fração
        return ((seconds - NTP_EPOCH) * 1000L) + ((fraction * 1000L) / 0x100000000L); // Converte para milissegundos
    }

    // Métodos para manipular timestamps específicos do protocolo NTP

    // Define o Transmit Timestamp (quando o pacote é enviado pelo servidor)
    public void setTransmitTimestamp(long millis) {
        setTimestamp(millis, 40);
    }
    public long getTransmitTimestamp() {
        return getTimestamp(40);
    }

    // Define o Receive Timestamp (quando o servidor recebe o pedido do cliente)
    public void setReceiveTimestamp(long millis) {
        setTimestamp(millis, 32);
    }
    public long getReceiveTimestamp() {
        return getTimestamp(32);
    }

    // Define o Originate Timestamp (timestamp enviado pelo cliente no pedido)
    public void setOriginateTimestamp(long millis) {
        setTimestamp(millis, 24);
    }
    public long getOriginateTimestamp() {
        return getTimestamp(24);
    }

    // Define o Reference Timestamp (última vez que o servidor foi sincronizado)
    public void setReferenceTimestamp(long millis) {
        setTimestamp(millis, 16);
    }
    public long getReferenceTimestamp() {
        return getTimestamp(16);
    }

    // Define e obtém o Root Delay (tempo de ida e volta do servidor até sua referência de tempo)
    public void setRootDelay(int delay) {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        buffer.putInt(4, delay);
    }
    public int getRootDelay() {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        return buffer.getInt(4);
    }

    // Define e obtém o Root Dispersion (variação de tempo estimada do servidor)
    public void setRootDispersion(int dispersion) {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        buffer.putInt(8, dispersion);
    }
    public int getRootDispersion() {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        return buffer.getInt(8);
    }

    // Define e obtém o Reference Identifier (identificação da referência do servidor)
    public void setReferenceIdentifier(int identifier) {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        buffer.putInt(12, identifier);
    }
    public int getReferenceIdentifier() {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        return buffer.getInt(12);
    }

    // Define e obtém o Stratum (nível hierárquico do servidor NTP)
    public void setStratum(byte stratum) {
        data[1] = stratum;
    }
    public byte getStratum() {
        return data[1];
    }

    // Define e obtém o Mode (3 para cliente, 4 para servidor)
    public void setMode(byte mode) {
        data[0] = (byte) ((data[0] & 248) | (mode & 7)); // Mantém os bits superiores e altera apenas os 3 inferiores
    }
    public byte getMode() {
        return (byte) (data[0] & 7); // Retorna apenas os 3 bits inferiores
    }
}
