package com.github.gusarov.carpc;

import android.util.Log;

import java.io.IOException;

public class StreamReader {
    static final String TAG = StreamReader.class.getSimpleName();

    public static StreamReader create(IBlockReader reader) {
        return new StreamReader(reader, 4*1024);
    }

    public static StreamReader create(IBlockReader reader, int bufSize) {
        return new StreamReader(reader, bufSize);
    }

    private IBlockReader _reader;
    private RingBuffer _readRingBuf;
    private byte[] _readTmpBuf;

    public StreamReader(IBlockReader reader, int bufSize) {

        _reader = reader;
        _readRingBuf = new RingBuffer(bufSize);
        _readTmpBuf = new byte[bufSize];
    }

    void callRead() throws IOException {
        int a = _readRingBuf.getSizeAvailable();
        int n = _reader.read(_readTmpBuf, a);
        if (n > a) {
            throw new IOException("Read more than allowed");
        }
        _readRingBuf.write(_readTmpBuf, 0, n);
    }

    boolean getIsEmpty() {
        return _readRingBuf.getSzieFilled() == 0;
    }

    // read byte or -1 if nothing
    int tryReadAndMove() throws IOException {
        synchronized (_readRingBuf) {
            if (getIsEmpty()) {
                callRead();
            }
            if (getIsEmpty()) {
                return -1;
            }
            byte[] bb = _readRingBuf.read(1);
            return bb[0];
        }
    }

    public byte ReadByte() throws IOException {
        int r = tryReadAndMove();
        if (r < 0) {
            throw new IOException("Stream is empty");
        }
        return (byte)r;
    }

    /*
    int ReadInt() {
        return 0;
    }

    String ReadSizedString() {
        return "";
    }
    */

    String readLine() throws IOException {
        // analyze buf
        int eol = endOfLineIndex();
        if (eol >= 0) {
            byte[] r = _readRingBuf.read(eol);
            _readRingBuf.read(2); // \r\n
            return new String(r);
        }

        // fill until cr lf appeared
        breakWhile: do {
            int r = _reader.read(_readTmpBuf, _readRingBuf.getSizeAvailable());
            // Log.d(TAG, "Received " + r + " bytes");
            if (r > 0) {
                _readRingBuf.write(_readTmpBuf, 0, r);
                for (int i = 0; i < r; i++) {
                    if (_readTmpBuf[i] == '\n' || _readTmpBuf[i] == '\r') {
                        break breakWhile; // enter detected, exit quickly
                    }
                }
            } else {

            }
        } while (true);

        // analyze buf and consume string from it
        eol = endOfLineIndex();
        if (eol >= 0) {
            byte[] r = _readRingBuf.read(eol);
            _readRingBuf.read(2); // \r\n
            return new String(r);
        }
        return null;
    }

    // analyze buf
    int endOfLineIndex() {
        for (int i = 0, m = _readRingBuf.getSzieFilled()-1; i < m; i++) {
            byte b = _readRingBuf.getItem(i);
            if (b=='\r' /*|| b=='\n'*/) {
                b = _readRingBuf.getItem(i+1);
                if (/*b=='\r' ||*/ b=='\n') {
                    return i;
                }
            }
        }
        return -1;
    }

}

abstract class BlockReader implements IBlockReader {
    public int read(byte buf[], int maxLen) throws IOException {
        return read(buf, 0, maxLen);
    }

    public abstract int read(byte buf[], int offset, int maxLen) throws IOException;
}
