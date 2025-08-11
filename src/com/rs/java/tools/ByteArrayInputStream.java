package com.rs.java.tools;

import java.io.IOException;
import java.io.InputStream;

public class ByteArrayInputStream extends InputStream {
    private byte[] data;
    private int pos = 0;

    public ByteArrayInputStream(byte[] data) {
        this.data = data;
    }

    @Override
    public int read() throws IOException {
        if (pos >= data.length) return -1;
        return data[pos++] & 0xFF;
    }
}
