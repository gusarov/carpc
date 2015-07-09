package com.github.gusarov.carpc;

public interface IBlockReader {
    int read(byte buf[], int bufIndex, int maxLen);
}
