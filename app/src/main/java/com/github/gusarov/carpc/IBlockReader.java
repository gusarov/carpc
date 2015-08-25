package com.github.gusarov.carpc;

import java.io.IOException;

public interface IBlockReader {
    int read(byte buf[], int maxLen) throws IOException;
    int read(byte buf[], int offset, int maxLen) throws IOException;
}
