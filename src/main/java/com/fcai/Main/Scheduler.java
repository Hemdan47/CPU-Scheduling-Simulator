package com.fcai.Main;
import java.util.Collection;
import java.util.List;

public abstract class Scheduler {
    protected List<Process> processList;
    protected int contextSwitchingTime;
    public boolean addProcess(Process process) {
        return processList.add(process);
    }
    public int calculateAverageWaitingTime() {
        int totalWaitingTime = 0;
        for (Process process : processList) {
            totalWaitingTime += process.getWaitingTime();
        }
        return (int)Math.ceil((double)totalWaitingTime/processList.size());
    }
    public int calculateAverageTurnAroundTime() {
        int totalTurnAroundTime = 0;
        for (Process process : processList) {
            totalTurnAroundTime += process.calculateTurnAroundTime();
        }
        return (int)Math.ceil((double)totalTurnAroundTime/processList.size());
    }
    public int getContextSwitchingTime() {
        return contextSwitchingTime;
    }
    public abstract void execute();


}
