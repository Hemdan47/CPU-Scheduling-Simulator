package com.fcai.SJF;

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
    public void execute() {
        // Sort processes based on arrival time
        Collections.sort(processList, Comparator.comparingInt(Process::getArrivalTime));

        int currentTime = processList.getFirst().getArrivalTime();
        List<Process> executedProcesses = new ArrayList<>();

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
            currentTime += selectedProcess.getBurstTime();

            executedProcesses.add(selectedProcess);
        }

        // Output results
        System.out.println("Processes execution order:");
        int totalWaitingTime = 0;
        int totalTurnAroundTime = 0;

        for (Process p : executedProcesses) {
            System.out.printf("Process: %s, Waiting Time: %d, Turnaround Time: %d%n",
                    p.getName(), p.getWaitingTime(), p.calculateTurnAroundTime());
            totalWaitingTime += p.getWaitingTime();
            totalTurnAroundTime += p.calculateTurnAroundTime();
        }

        // Calculate averages
        double averageWaitingTime = (!executedProcesses.isEmpty()) ? (double) totalWaitingTime / executedProcesses.size() : 0;
        double averageTurnAroundTime = (!executedProcesses.isEmpty()) ? (double) totalTurnAroundTime / executedProcesses.size() : 0;

        // Print averages
        System.out.printf("Average Waiting Time: %.2f%n", averageWaitingTime);
        System.out.printf("Average Turnaround Time: %.2f%n", averageTurnAroundTime);
    }

}