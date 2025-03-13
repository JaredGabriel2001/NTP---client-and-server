package org.application.ntp;

import java.nio.ByteBuffer;

public class NtpPacket {
    private static final int PACKET_SIZE = 48;
    private static final long NTP_EPOCH = 2208988800L; // Diferença em segundos entre 1900 e 1970
    private final byte[] data;

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

    // Método auxiliar para definir um timestamp (64 bits: 32 bits de segundos e 32 bits de fração) em um offset específico.
    private void setTimestamp(long millis, int offset) {
        long seconds = millis / 1000L + NTP_EPOCH;
        long fraction = ((millis % 1000L) * 0x100000000L) / 1000L;
        ByteBuffer buffer = ByteBuffer.wrap(data);
        buffer.putInt(offset, (int) seconds);
        buffer.putInt(offset + 4, (int) fraction);
    }

    // Método auxiliar para extrair um timestamp (convertido para milissegundos) de um offset específico.
    private long getTimestamp(int offset) {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        long seconds = Integer.toUnsignedLong(buffer.getInt(offset));
        long fraction = Integer.toUnsignedLong(buffer.getInt(offset + 4));
        return ((seconds - NTP_EPOCH) * 1000L) + ((fraction * 1000L) / 0x100000000L);
    }

    // Transmit Timestamp (offset 40)
    public void setTransmitTimestamp(long millis) {
        setTimestamp(millis, 40);
    }

    public long getTransmitTimestamp() {
        return getTimestamp(40);
    }

    // Receive Timestamp (offset 32)
    public void setReceiveTimestamp(long millis) {
        setTimestamp(millis, 32);
    }

    public long getReceiveTimestamp() {
        return getTimestamp(32);
    }

    // Originate Timestamp (offset 24) – utilizado para copiar o timestamp enviado pelo cliente.
    public void setOriginateTimestamp(long millis) {
        setTimestamp(millis, 24);
    }

    public long getOriginateTimestamp() {
        return getTimestamp(24);
    }

    // Reference Timestamp (offset 16)
    public void setReferenceTimestamp(long millis) {
        setTimestamp(millis, 16);
    }

    public long getReferenceTimestamp() {
        return getTimestamp(16);
    }

    // Root Delay (offset 4)
    public void setRootDelay(int delay) {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        buffer.putInt(4, delay);
    }

    public int getRootDelay() {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        return buffer.getInt(4);
    }

    // Root Dispersion (offset 8)
    public void setRootDispersion(int dispersion) {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        buffer.putInt(8, dispersion);
    }

    public int getRootDispersion() {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        return buffer.getInt(8);
    }

    // Reference Identifier (offset 12)
    public void setReferenceIdentifier(int identifier) {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        buffer.putInt(12, identifier);
    }

    public int getReferenceIdentifier() {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        return buffer.getInt(12);
    }

    // Stratum (byte 1). Para servidores primários, normalmente usa-se 1.
    public void setStratum(byte stratum) {
        data[1] = stratum;
    }

    public byte getStratum() {
        return data[1];
    }

    // Configura o Mode (os 3 bits menos significativos do byte 0).
    // Exemplo: 3 para cliente e 4 para servidor.
    public void setMode(byte mode) {
        data[0] = (byte) ((data[0] & 248) | (mode & 7));
    }

    public byte getMode() {
        return (byte) (data[0] & 7);
    }
}