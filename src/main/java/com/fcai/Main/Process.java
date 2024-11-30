package com.fcai.Main;

public class Process {
    private String name;
    private String color;
    private int arrivalTime;
    private int waitingTime;
    private int burstTime;
    private int priority;
    private int completionTime;

    public Process(String name, String color, int arrivalTime, int burstTime, int priority) {
        this.name = name;
        this.color = color;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.priority = priority;
    }

    public String getName() {
        return name;
    }

    public int getWaitingTime() {
        return waitingTime;
    }

    public String getColor() {
        return color;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public int getBurstTime() {
        return burstTime;
    }

    public int getPriority() {
        return priority;
    }

    public void setWaitingTime(int waitingTime) {
        this.waitingTime = waitingTime;
    }

    public void setCompletionTime(int completionTime) {
        this.completionTime = completionTime;
    }
    public int calculateTurnAroundTime(){
        return completionTime - arrivalTime;
    }
}
