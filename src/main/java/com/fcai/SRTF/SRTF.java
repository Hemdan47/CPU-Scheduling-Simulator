package com.fcai.SRTF;

import com.fcai.Main.Process;
import com.fcai.Main.Scheduler;

import java.util.*;

public class SRTF extends Scheduler {

    public SRTF(List<Process> list) {
        this.contextSwitchingTime = 1;
        this.processList = new ArrayList<>(list);
    }

    @Override
    public void execute() {

        //sort all the processes on their arrival time
        Collections.sort(processList, Comparator.comparingInt(Process::getArrivalTime));

        int numberOfProcesses = processList.size();
        int currentTime = 0;

        //ready queue contains process sorted with burst then priority
        PriorityQueue<Process> readyQueue = new PriorityQueue<>(Comparator.comparingInt(Process::getBurstTime).thenComparingInt(Process::getPriority));

        //executed processes to add processes after then are completed in their completion order
        List<Process> executedProcesses = new ArrayList<>();

        int completedProcesses = 0;

        //save initial burst to calculate waiting time in the end
        Map<String, Integer> initialBurstTimeMap = new HashMap<>();
        for (Process process : processList) {
            initialBurstTimeMap.put(process.getName(), process.getBurstTime());
        }

        Process lastProcess = null; //  track the last process being executed
        int executionStartTime = -1; // track when a process starts execution (begging of execution)

        System.out.printf("Process    Arrival   Remaining Burst Time    Time Started    Time Ended \n");

        while (completedProcesses < numberOfProcesses) {
            for (Process process : processList) {
                if (process.getArrivalTime() <= currentTime && process.getBurstTime() > 0 && !readyQueue.contains(process)) {
                    readyQueue.add(process);
                }
            }

            Process currentProcess = readyQueue.poll();

            // if no process is in ready queue, move time forward
            if (currentProcess == null) {
                currentTime++;
                continue;
            }

            // in case of presence context switching
            if (lastProcess != null && !lastProcess.equals(currentProcess)) {
                if (executionStartTime != -1) {
                    // log the previous process's execution info
                    System.out.printf("%-10s%-15d%-25d%-15d%-15d\n",
                            lastProcess.getName(),
                            lastProcess.getArrivalTime(),
                            lastProcess.getBurstTime(),
                            executionStartTime,
                            currentTime);
                }

                // context switching
                System.out.printf("Switching from process %s to process %s at time %d\n",
                        lastProcess.getName(), currentProcess.getName(), currentTime);
                currentTime += contextSwitchingTime;
                executionStartTime = currentTime;
            } else if (lastProcess == null) {
                executionStartTime = currentTime; // first process to start execution
            }

            // execute the process for 1 unit of time
            currentProcess.setBurstTime(currentProcess.getBurstTime() - 1);
            currentTime++;

            // if the process is completed, log it then add it to completed processes
            if (currentProcess.getBurstTime() == 0) {
                System.out.printf("%-10s%-15d%-25d%-15d%-15d\n",
                        currentProcess.getName(),
                        currentProcess.getArrivalTime(),
                        0,
                        executionStartTime,
                        currentTime);
                currentProcess.setCompletionTime(currentTime);
                executedProcesses.add(currentProcess);
                completedProcesses++;
                executionStartTime = -1; // Reset for the next process
            } else {
                // Re-add the process to the queue
                readyQueue.add(currentProcess);
            }

            lastProcess = currentProcess; // Update the last executed process
        }


        System.out.println("\nProcesses completion order:");
        int totalWaitingTime = 0;
        int totalTurnAroundTime = 0;

        for (Process p : executedProcesses) {
            int turnAroundTime = p.getCompletionTime() - p.getArrivalTime();
            int initialBurstTime = initialBurstTimeMap.get(p.getName());
            int waitingTime = turnAroundTime - initialBurstTime;

            p.setWaitingTime(waitingTime);
            totalWaitingTime += waitingTime;
            totalTurnAroundTime += turnAroundTime;

            System.out.printf("Process: %s, Waiting Time: %d, Turnaround Time: %d\n",
                    p.getName(), waitingTime, turnAroundTime);
        }
        processList = executedProcesses;

        System.out.printf("Average Waiting Time: %.2f%n", (double) calculateAverageWaitingTime());
        System.out.printf("Average Turnaround Time: %.2f%n", (double) calculateAverageTurnAroundTime());
    }
}
