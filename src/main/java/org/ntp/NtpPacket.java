package org.ntp;

import java.nio.ByteBuffer;

public class NtpPacket {
    private static final int PACKET_SIZE = 48;
    // Diferença (em segundos) entre a época NTP (1900) e a época Unix (1970)
    private static final long NTP_EPOCH = 2208988800L;
    private final byte[] data;

    public NtpPacket() {
        data = new byte[PACKET_SIZE];
        // Para o modo oficial:
        // O primeiro byte contém: LI = 0, VN = 4 e Mode = 3 (cliente);
        // Isso resulta em 0x23 (35 decimal). (Versão 4, Client Mode)
        data[0] = 0x23;
    }

    public byte[] toByteArray() {
        return data;
    }

    // Escreve o timestamp de transmissão no pacote (offset 40)
    public void setTransmitTimestamp(long millis) {
        long seconds = millis / 1000 + NTP_EPOCH;
        long fraction = ((millis % 1000) * 0x100000000L) / 1000;
        ByteBuffer buffer = ByteBuffer.wrap(data);
        buffer.putInt(40, (int) seconds);
        buffer.putInt(44, (int) fraction);
    }

    // Extrai o timestamp de transmissão (do servidor) do pacote (offset 40)
    public long getServerTime() {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        long seconds = Integer.toUnsignedLong(buffer.getInt(40));
        long fraction = Integer.toUnsignedLong(buffer.getInt(44));
        return ((seconds - NTP_EPOCH) * 1000) + ((fraction * 1000) / 0x100000000L);
    }
}
