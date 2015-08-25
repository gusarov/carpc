package com.github.gusarov.carpc;

import java.io.IOException;

public class RingBuffer {
    public RingBuffer() {
        buf = new byte[4 * 1024];
    }

    public RingBuffer(int size) {
        buf = new byte[size];
    }

    byte buf[];
    int bufStart; // 0..9
    int bufFillLen; // 0..10

    int getBufEnd(boolean repos) {
        int end = bufStart + bufFillLen;
        if (end > buf.length && repos) {
            end -= buf.length;
        }
        return end;
    }

    public byte getItem(int index) {
        if (index < 0) {
            throw new IllegalArgumentException();
        }
        if (index >= bufFillLen) {
            throw new IllegalArgumentException();
        }
        index += bufStart;
        if (index >= buf.length) {
            index -= buf.length;
        }
        return buf[index];
    }

    public int getSzieFilled() {
        return bufFillLen;
    }

    public int getCapacity() {
        return buf.length;
    }

    public int getSizeAvailable() {
        return buf.length - bufFillLen;
    }

    public void write(byte[] src) throws IOException {
        write(src, 0, src.length);
    }

    public void write(byte[] src, int srcOffset, int srcLen) throws IOException {
        if (getSizeAvailable() < srcLen) {
            throw new IOException("Not enough space");
        }
        if (srcOffset < 0) {
            throw new IllegalArgumentException("srcOffset < 0");
        }
        if (srcLen < 0) {
            throw new IllegalArgumentException("srcLen < 0");
        }
        if (srcOffset + srcLen > src.length) {
            throw new IllegalArgumentException("srcOffset + srcLen > src.length");
        }

        // dummy
        for (int i = 0; i < srcLen; i++) {
            int writeTo = bufStart + bufFillLen++;
            if (writeTo >= buf.length) {
                writeTo -= buf.length;
            }

            if (writeTo > buf.length) {
                throw new IllegalStateException("writeTo > buf.length");
            }

            if (writeTo < 0) {
                throw new IllegalStateException("writeTo < 0");
            }

            buf[writeTo] = src[srcOffset + i];
        }

        if (bufFillLen > buf.length) {
            throw new IllegalStateException("bufFillLen > buf.length");
        }

        if (bufFillLen < 0) {
            throw new IllegalStateException("bufFillLen < 0");
        }

        //var end = getBufEnd(false)
        //System.arraycopy(src, srcOffset, buf, );
    }

    public int read(byte[] trg, int trgOffset, int maxLen) throws IOException {
        int num = Math.min(maxLen, getSzieFilled());
        for (int i = 0; i < num; i++) {
            trg[trgOffset + i] = buf[bufStart];
            bufStart++;
            bufFillLen--;
            if (bufStart >= buf.length) {
                bufStart -= buf.length;
            }

            if (bufStart >= buf.length) {
                throw new IllegalStateException("bufStart >= buf.length");
            }
            if (bufStart < 0) {
                throw new IllegalStateException("bufStart < 0");
            }
        }
        if (getSzieFilled() > buf.length) {
            throw new IllegalStateException("getSzieFilled() > buf.length");
        }
        if (getSzieFilled() < 0) {
            throw new IllegalStateException("getSzieFilled() < 0");
        }
        return num;
    }

    public byte[] read(int maxLen) throws IOException {
        int num = Math.min(maxLen, getSzieFilled());
        byte[] r = new byte[num];
        read(r, 0, num);
        return r;
    }

}