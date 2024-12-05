package com.fcai.Main;

import javafx.scene.paint.Color;

import java.util.Objects;

public class Process {
    private final String name;
    private final Color color;
    private final int arrivalTime;
    private int waitingTime;
    private int burstTime;
    private final int priority;
    private int completionTime;
    public int quantum;
    private int age;

    public Process(String name, Color color, int burstTime, int arrivalTime, int priority, int quantum) {
        this.name = name;
        this.color = color;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.priority = priority;
        this.quantum = quantum;
        this.age = 0;
    }

    public String getName() {
        return name;
    }

    public int getWaitingTime() {
        return waitingTime;
    }

    public Color getColor() {
        return color;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public int getBurstTime() {
        return burstTime;
    }

    public void setBurstTime(int burstTime) {
        this.burstTime = burstTime;
    }

    public int getPriority() {
        return priority;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Process process = (Process) o;
        return arrivalTime == process.arrivalTime && waitingTime == process.waitingTime && burstTime == process.burstTime && priority == process.priority && completionTime == process.completionTime && Objects.equals(name, process.name) && Objects.equals(color, process.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, color, arrivalTime, waitingTime, burstTime, priority, completionTime);
    }

    public void setWaitingTime(int waitingTime) {
        this.waitingTime = waitingTime;
    }

    public int getCompletionTime() {
        return completionTime;
    }

    public void setCompletionTime(int completionTime) {
        this.completionTime = completionTime;
    }

    public int calculateTurnAroundTime() {
        return completionTime - arrivalTime;
    }

    public int getQuantum() {
        return quantum;
    }

    public void setQuantum(int quantum) {
        this.quantum = quantum;
    }

}
