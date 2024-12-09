package com.fcai.SRTF;

import com.fcai.Main.GUIGraphNeeds;
import com.fcai.Main.GUIStatistics;
import com.fcai.Main.Process;
import com.fcai.Main.Scheduler;

import java.util.*;

public class SRTFstarvation extends Scheduler {

    public SRTFstarvation(List<Process> list, int contextSwitch) {
        this.processList = new ArrayList<>(list);
        this.contextSwitchingTime = contextSwitch;
        this.threshold = 8;
    }


    @Override
    public void execute() {

        //sort all the processes on their arrival time
        Collections.sort(processList, Comparator.comparingInt(Process::getArrivalTime));

        int numberOfProcesses = processList.size();
        int currentTime = 0;
        int runningDuration = 1;

        //ready queue contains process sorted with burst then priority
        PriorityQueue<Process> readyQueue = new PriorityQueue<>(Comparator.comparingInt(Process::getBurstTime).thenComparingInt(Process::getPriority));

        //executed processes to add processes after then are completed in their completion order
        List<Process> executedProcesses = new ArrayList<>();

        int completedProcesses = 0;

        //save initial burst to calculate waiting time in the end
        //Map<String, Integer> initialBurstTimeMap = new HashMap<>();
//        for (Process process : processList) {
//            initialBurstTimeMap.put(process.getName(), process.getBurstTime());
//        }

        Process lastProcess = null; //  track the last process being executed
        int executionStartTime = -1; // track when a process starts execution (begging of execution)

        System.out.printf("Process    Arrival   Remaining Burst Time       Time Started      Time Ended \n");

        while (completedProcesses < numberOfProcesses) {
            for (Process process : processList) {
                if (process.getArrivalTime() <= currentTime && process.getBurstTime() > 0 && !readyQueue.contains(process)) {
                    readyQueue.add(process);
                }
            }
            for (Process process : readyQueue) {
                if (!process.equals(lastProcess)) {
                    process.setAge(process.getAge() + 1);
                    //System.out.println(process.getName() +" "+ process.getAge());
                }
            }
            Process starvedProcess = null;

            List<Process> thresholdExceeded = new ArrayList<>();
            for (Process process : readyQueue) {
                if (process.getAge() >= threshold) {
                    thresholdExceeded.add(process);
                }

            }
            thresholdExceeded.sort(Comparator.comparingInt(Process::getAge).thenComparingInt(Process::getPriority));
            if (!thresholdExceeded.isEmpty()) {
                starvedProcess = thresholdExceeded.get(0);
            }
            for (Process process : readyQueue) {
                if (process.getAge() >= threshold) {
                    starvedProcess = process;
                    break;
                }
            }
            Process currentProcess;

            if (starvedProcess != null) {
                currentProcess = starvedProcess;
                readyQueue.remove(starvedProcess);
            } else {
                currentProcess = readyQueue.poll();
            }

            // if no process is in ready queue, move time forward
            if (currentProcess == null) {
                currentTime++;
                continue;
            }
            //check if a process is starving and execute its quantum
            if (currentProcess.getAge() >= threshold) {
                if (lastProcess != null && executionStartTime != -1) {
                    System.out.printf("%-15s%-15d%-25d%-15d%-15d\n",
                            lastProcess.getName(),
                            lastProcess.getArrivalTime(),
                            lastProcess.getBurstTime(),
                            executionStartTime,
                            currentTime);
                    System.out.printf("Switching from process %s to process %s at time %d to avoid starvation\n",
                            lastProcess.getName(), currentProcess.getName(), currentTime);
                }

                currentTime += contextSwitchingTime;
                executionStartTime = currentTime;
                int burst = currentProcess.getBurstTime();
                int q = currentProcess.getQuantum();
                q= Math.min(q,burst);
                int executionTime =q;
                while (q > 0) {
                    currentProcess.setBurstTime(currentProcess.getBurstTime() - 1);
                    q--;
                }

                for (int i = 0; i < executionTime; i++) {
                    // i+1
                     guiGraphNeeds.add(new GUIGraphNeeds(currentProcess, currentTime + i, 1));
                }
                for (Process p:readyQueue){
                    p.setAge(p.getAge()+executionTime);
                }
                currentTime += (executionTime);
//                for (Process process: readyQueue){
//                    process.setAge(process.getAge()+executionTime);
//                }
                currentProcess.setAge(0);
                if (currentProcess.getBurstTime() == 0) {
                    System.out.printf("%-15s%-15d%-25d%-15d%-15d\n",
                            currentProcess.getName(),
                            currentProcess.getArrivalTime(),
                            0,
                            executionStartTime,
                            currentTime);
                    currentProcess.setCompletionTime(currentTime);
                    executedProcesses.add(currentProcess);
                    completedProcesses++;
                    executionStartTime = -1; // Reset for the next process
                    System.out.println(currentProcess.getName() +" is completed");
                } else {
                    // Re-add the process to the queue
                    readyQueue.add(currentProcess);
                }

                lastProcess = currentProcess; // Update the last executed process
                continue;

            }


            // in case of presence context switching
            if (lastProcess != null && !lastProcess.equals(currentProcess)) {
                if (executionStartTime != -1) {
                    // log the previous process's execution info
                    System.out.printf("%-15s%-15d%-25d%-15d%-15d\n",
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

            guiGraphNeeds.add(new GUIGraphNeeds(currentProcess, currentTime, runningDuration));

            // execute the process for 1 unit of time
            currentProcess.setBurstTime(currentProcess.getBurstTime() - 1);
            currentTime++;

            // if the process is completed, log it then add it to completed processes
            if (currentProcess.getBurstTime() == 0) {
                System.out.printf("%-15s%-15d%-25d%-15d%-15d\n",
                        currentProcess.getName(),
                        currentProcess.getArrivalTime(),
                        0,
                        executionStartTime,
                        currentTime);
                currentProcess.setCompletionTime(currentTime);
                executedProcesses.add(currentProcess);
                completedProcesses++;
                executionStartTime = -1; // reset for the next process
                System.out.println(currentProcess.getName() +" is completed");

            } else {
                // re-add the process to the ready queue
                readyQueue.add(currentProcess);
            }

            lastProcess = currentProcess; // Update the last executed process
        }


        System.out.println("\nProcesses completion order:");
        int totalWaitingTime = 0;
        int totalTurnAroundTime = 0;

        for (Process p : executedProcesses) {
            int turnAroundTime = p.getCompletionTime() - p.getArrivalTime();
            int initialBurstTime = p.getInitialBurstTime();
            int waitingTime = turnAroundTime - initialBurstTime;

            p.setWaitingTime(waitingTime);
            totalWaitingTime += waitingTime;
            totalTurnAroundTime += turnAroundTime;

            System.out.printf("Process: %s, Waiting Time: %d, Turnaround Time: %d\n",
                    p.getName(), waitingTime, turnAroundTime);
        }
        processList = executedProcesses;

        double avWaitingTime = calculateAverageWaitingTime();
        double avTurnaroundTime = calculateAverageTurnAroundTime();

        System.out.printf("Average Waiting Time: %.2f%n", avWaitingTime);
        System.out.printf("Average Turnaround Time: %.2f%n", avTurnaroundTime);

        guiStatistics = new GUIStatistics("SRTF", avWaitingTime, avTurnaroundTime);

    }
}