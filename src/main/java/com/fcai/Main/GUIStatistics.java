package com.fcai.Main;

public class GUIStatistics {
    private final double avWaitingTime;
    private final double avTurnaroundTime;
    private final String scheduleName;


    public GUIStatistics(String scheduleName,double avWaitingTime, double avTurnaroundTime) {
        this.avWaitingTime = avWaitingTime;
        this.avTurnaroundTime = avTurnaroundTime;
        this.scheduleName = scheduleName;
    }

    public double getAvWaitingTime() {
        return avWaitingTime;
    }

    public double getAvTurnaroundTime() {
        return avTurnaroundTime;
    }

    public String getScheduleName() {
        return scheduleName;
    }
}
