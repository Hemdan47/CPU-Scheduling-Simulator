package com.fcai.SJF;

import com.fcai.Main.Duration;
import com.fcai.Main.GUIStatistics;
import com.fcai.Main.Process;
import com.fcai.Main.Scheduler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SJF extends Scheduler {
    public SJF(List<Process> processList) {
        this.processList = new ArrayList<>(processList);
        this.contextSwitchingTime = 0;
    }

    @Override
    public ArrayList<Duration> execute() {
        ArrayList<Duration> durations = new ArrayList<>();
        // Sort processes based on arrival time
        Collections.sort(processList, Comparator.comparingInt(Process::getArrivalTime));

        int currentTime = 0;
        List<Process> executedProcesses = new ArrayList<>();

        System.out.printf("Process    Arrival   Remaining Burst Time    Time Started    Time Ended \n");

        while (!processList.isEmpty()) {
            // Get the list of processes that have arrived
            List<Process> availableProcesses = new ArrayList<>();
            for (Process process : processList) {
                if (process.getArrivalTime() <= currentTime) {
                    availableProcesses.add(process);
                }
            }

            if (availableProcesses.isEmpty()) {
                // If no process is available, advance time
                // then loop on the processes again to get the first one that arrived
                currentTime++;
                continue;
            }

            // Sort available processes by burst time (SJF)
            availableProcesses.sort(Comparator.comparingInt(Process::getBurstTime));

            // Select the process with the shortest burst time
            Process selectedProcess = availableProcesses.get(0);
            processList.remove(selectedProcess);

            // completion time = current time when the process starts
            // executing and the burst time of that process
            selectedProcess.setCompletionTime(currentTime + selectedProcess.getBurstTime());
            selectedProcess.setWaitingTime(currentTime - selectedProcess.getArrivalTime());
            durations.add(new Duration(selectedProcess, currentTime, selectedProcess.getBurstTime()));

            int executionStartTime = currentTime;
            currentTime += selectedProcess.getBurstTime();
            System.out.printf("%-10s%-15d%-25d%-15d%-15d\n",
                    selectedProcess.getName(),
                    selectedProcess.getArrivalTime(),
                    selectedProcess.getBurstTime(),
                    executionStartTime,
                    currentTime);

            executedProcesses.add(selectedProcess);
        }

        // Output results
        System.out.println("Processes execution order:");


        for (Process p : executedProcesses) {
            System.out.printf("Process: %s, Waiting Time: %d, Turnaround Time: %d%n",
                    p.getName(), p.getWaitingTime(), p.calculateTurnAroundTime());

        }
        processList = executedProcesses;

        double avWaitingTime=calculateAverageWaitingTime();
        double avTurnaroundTime=calculateAverageTurnAroundTime();

        System.out.printf("Average Waiting Time: %.2f%n", avWaitingTime);
        System.out.printf("Average Turnaround Time: %.2f%n", avTurnaroundTime);

        guiStatistics=new GUIStatistics("SJF",avWaitingTime , avTurnaroundTime);
        return durations;
    }

}
