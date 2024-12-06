package com.fcai.SRTF;

import com.fcai.Main.GUIGraphNeeds;
import com.fcai.Main.GUIStatistics;
import com.fcai.Main.Process;
import com.fcai.Main.Scheduler;

import java.util.*;

public class SRTF extends Scheduler {
    //private List<GUINeeds> guiNeeds = new ArrayList<>();

    public SRTF(List<Process> processList,int contextSwitch) {
        this.contextSwitchingTime = contextSwitch;
        this.processList = new ArrayList<>(processList);
    }

    @Override
    public void execute() {

        //sort all the processes on their arrival time
        Collections.sort(processList, Comparator.comparingInt(Process::getArrivalTime));

        int numberOfProcesses = processList.size();
        int currentTime = 0;
        int runningDuration=1;

        //ready queue contains process sorted with burst then priority
        PriorityQueue<Process> readyQueue = new PriorityQueue<>(Comparator.comparingInt(Process::getBurstTime).thenComparingInt(Process::getPriority));

        //executed processes to add processes after then are completed in their completion order
        List<Process> executedProcesses = new ArrayList<>();

        int completedProcesses = 0;

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

                runningDuration=currentTime; //Takiiii
                currentTime += contextSwitchingTime;
                runningDuration=currentTime-runningDuration;
                executionStartTime = currentTime;

            } else if (lastProcess == null) {
                executionStartTime = currentTime; // first process to start execution
            }
            guiGraphNeeds.add(new GUIGraphNeeds(currentProcess , currentTime ,runningDuration));

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
            int waitingTime = turnAroundTime - p.getInitialBurstTime();

            p.setWaitingTime(waitingTime);
            totalWaitingTime += waitingTime;
            totalTurnAroundTime += turnAroundTime;

            System.out.printf("Process: %s, Waiting Time: %d, Turnaround Time: %d\n",
                    p.getName(), waitingTime, turnAroundTime);
        }
        processList = executedProcesses;

        double avWaitingTime=calculateAverageWaitingTime();
        double avTurnaroundTime=calculateAverageTurnAroundTime();

        System.out.printf("Average Waiting Time: %.2f%n", avWaitingTime);
        System.out.printf("Average Turnaround Time: %.2f%n", avTurnaroundTime);

        guiStatistics=new GUIStatistics("SRTF",avWaitingTime , avTurnaroundTime);
    }
}
