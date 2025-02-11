package com.fcai.Main;

public class Duration {
    private final Process process;
    private final int currentTime;
    private final int runningDuration;

    public Duration(Process process, int currentTime, int runningDuration) {
        this.process = process;
        this.currentTime = currentTime;
        this.runningDuration = runningDuration;
    }

    public Process getProcess() {
        return process;
    }

    public int getCurrentTime() {
        return currentTime;
    }

    public int getRunningDuration() {
        return runningDuration;
    }
}
