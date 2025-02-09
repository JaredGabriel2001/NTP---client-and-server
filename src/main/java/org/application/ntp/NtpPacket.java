package org.application.ntp;

import java.nio.ByteBuffer;

public class NtpPacket {
    private static final int PACKET_SIZE = 48;
    private static final long NTP_EPOCH = 2208988800L;
    private final byte[] data;

    public NtpPacket() {
        data = new byte[PACKET_SIZE];
        data[0] = 0b00100011; // Leap Indicator, Version, Mode
    }

    public byte[] toByteArray() {
        return data;
    }

    public void setTransmitTimestamp(long millis) {
        long seconds = millis / 1000 + NTP_EPOCH;
        long fraction = ((millis % 1000) * 0x100000000L) / 1000;
        ByteBuffer buffer = ByteBuffer.wrap(data);
        buffer.putInt(40, (int) seconds);
        buffer.putInt(44, (int) fraction);
    }

    public long getReceiveTimestamp() {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        long seconds = Integer.toUnsignedLong(buffer.getInt(32));
        long fraction = Integer.toUnsignedLong(buffer.getInt(36));
        return ((seconds - NTP_EPOCH) * 1000) + ((fraction * 1000) / 0x100000000L);
    }
}
