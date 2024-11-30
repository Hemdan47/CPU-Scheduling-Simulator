package com.fcai.Main;
import java.util.Collection;
import java.util.List;

public abstract class Scheduler {
    private Collection<Process> processList;
    private int contextSwitchingTime;

    public boolean addProcess(Process process) {
        return processList.add(process);
    }

    public int calculateAverageWaitingTime() {
        int totalWaitingTime = 0;
        for (Process process : processList) {
            totalWaitingTime += process.getWaitingTime();
        }
        return (int)Math.ceil(totalWaitingTime/processList.size());
    }
    public int calculateAverageTurnAroundTime() {
        int totalTurnAroundTime = 0;
        for (Process process : processList) {
            totalTurnAroundTime += process.calculateTurnAroundTime();
        }
        return (int)Math.ceil(totalTurnAroundTime/processList.size());
    }

    public int getContextSwitchingTime() {
        return contextSwitchingTime;
    }

    public abstract void execute();


}
