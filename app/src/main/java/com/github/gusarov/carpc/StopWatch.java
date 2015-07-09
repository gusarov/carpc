package com.github.gusarov.carpc;

public class StopWatch {
    public static StopWatch StartNew() {
        StopWatch sw = new StopWatch();
        return sw;
    }

    private void StopWatch() {
        start();
    }

    private long startTime;
    private long stopTime;

    public void start() {
        startTime = System.currentTimeMillis();
        stopTime = 0;
    }

    public void stop() {
        stopTime = System.currentTimeMillis();
    }

    public long getElapsedMilliseconds() {
        return (stopTime == 0 ? System.currentTimeMillis() : stopTime) - startTime;
    }

    public long getElapsedSeconds() {
        return getElapsedMilliseconds() / 1000;
    }
}