package com.github.gusarov.carpc;

import java.io.IOException;

/**
 * Created by xkip on 2015-07-09.
 */
public class StreamReader {
    public static StreamReader Create(IBlockReader reader) {
        return new StreamReader(reader, 4*1024);
    }

    public static StreamReader Create(IBlockReader reader, int bufSize) {
        return new StreamReader(reader, bufSize);
    }

    private IBlockReader reader;
    byte buf[];
    int bufStart;
    int bufEnd;

    public StreamReader(IBlockReader reader, int bufSize) {

        this.reader = reader;
        buf = new byte[bufSize];
    }

    void advanceReadPosition(int bytes) throws IOException {
        int available = bufEnd - bufStart;
        // cycled buffer
        if (bufEnd < bufStart ) {
            available += buf.length;
        }
        if (bytes > available) {
            throw new IOException("Unable to move read position far than fill position");
        }
        bufStart += bytes;
        if (bufStart >= buf.length) {
            bufStart -= buf.length;
        }
    }

    void advanceWritePosition(int bytes) throws IOException {
    }

    boolean getIsEmpty() {
        return bufStart == bufEnd;
    }

    void callRead() {

    }

    // read byte or -1 if nothing
    int TryReadAndMove() throws IOException {
        synchronized (buf) {
            if (getIsEmpty()) {
                callRead();
            }
            if (getIsEmpty()) {
                return -1;
            }
            byte b = buf[bufStart];
            advanceReadPosition(1);
            return b;
        }
    }

    public byte ReadByte() throws IOException {
        int r = TryReadAndMove();
        if (r < 0) {
            throw new IOException("Stream is empty");
        }
        return (byte)r;
    }

    int ReadInt() {
        return 0;
    }

    String ReadSizedString() {
        return "";
    }

    String ReadLine() {
        return "";
    }
}

