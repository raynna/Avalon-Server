package com.rs.core.cache;

public class Buffer {
    public byte[] data;
    public int position;

    public Buffer(byte[] data) {
        this.data = data;
        this.position = 0;
    }

    public int readUnsignedByte() {
        return data[position++] & 0xFF;
    }

    public byte readByte() {
        return data[position++];
    }

    public int readUnsignedShort() {
        int value = ((data[position++] & 0xFF) << 8) | (data[position++] & 0xFF);
        return value & 0xFFFF;
    }

    public int read24BitInt() {
        int value = ((data[position++] & 0xFF) << 16) |
                    ((data[position++] & 0xFF) << 8) |
                    (data[position++] & 0xFF);
        return value;
    }
}
