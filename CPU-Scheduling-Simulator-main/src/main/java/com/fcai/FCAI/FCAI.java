package com.fcai.FCAI;
import com.fcai.Main.*;
import com.fcai.Main.Process;


import java.util.*;

public class FCAI extends Scheduler {
    private double v1; // V1 = last arrival time of all processes / 10
    private double v2; // V2 = max burst time of all processes / 10

    public FCAI(List<Process> processes) {
        this.processList = new ArrayList<>(processes);
        this.contextSwitchingTime = 0; // No context switching time as per your request
        calculateV1V2();
    }

    private void calculateV1V2() {
        int lastArrivalTime = processList.stream().mapToInt(Process::getArrivalTime).max().orElse(0);
        int maxBurstTime = processList.stream().mapToInt(Process::getBurstTime).max().orElse(0);
        this.v1 = lastArrivalTime / 10.0;
        this.v2 = maxBurstTime / 10.0;
    }

    private double calculateFCAIFactor(Process process) {
        int priority = process.getPriority();
        int arrivalTime = process.getArrivalTime();
        int remainingBurstTime = process.getBurstTime();
        return (10 - priority) + Math.ceil(arrivalTime / v1) + Math.ceil(remainingBurstTime / v2);
    }

    @Override
    public void execute() {
        PriorityQueue<Process> readyQueue = new PriorityQueue<>(
                Comparator.comparingDouble(this::calculateFCAIFactor)
        );

        int currentTime = 0;
        List<Process> completedProcesses = new ArrayList<>();
        Map<String, Integer> originalBurstTimes = new HashMap<>();
        for (Process process : processList) {
            originalBurstTimes.put(process.getName(), process.getBurstTime());
        }

        while (!processList.isEmpty() || !readyQueue.isEmpty()) {
            // Move arrived processes to the ready queue
            Iterator<Process> it = processList.iterator();
            while (it.hasNext()) {
                Process process = it.next();
                if (process.getArrivalTime() <= currentTime) {
                    readyQueue.add(process);
                    it.remove();
                }
            }

            if (readyQueue.isEmpty()) {
                currentTime++; // Increment time if no process is ready
                continue;
            }

            Process currentProcess = readyQueue.poll();
            System.out.println(currentProcess.getName() + ":  " + calculateFCAIFactor(currentProcess));
            int quantum = currentProcess.getQuantum();
            int first40Percent = (int) Math.ceil(0.4 * quantum);
            int remainingQuantum = quantum - first40Percent;

            // Execute the first 40% non-preemptively
            int executionTime = Math.min(first40Percent, currentProcess.getBurstTime());
            currentProcess.setBurstTime(currentProcess.getBurstTime() - executionTime);
            currentTime += executionTime;

            // If the process finishes during the first 40%, update completion time
            if (currentProcess.getBurstTime() == 0) {
                currentProcess.setCompletionTime(currentTime);
                completedProcesses.add(currentProcess);
                continue;
            }

            // Preemptive execution for the remaining quantum
            for (int t = 0; t < remainingQuantum; t++) {
                // Add newly arrived processes to the queue
                /// ////
                Iterator<Process> newIt = processList.iterator();
                while (newIt.hasNext()) {
                    Process process = newIt.next();
                    if (process.getArrivalTime() <= currentTime) {
                        readyQueue.add(process);
                        newIt.remove();
                    }
                }

                // Check for higher-priority process
                Process highestPriorityProcess = readyQueue.peek();
                if (highestPriorityProcess != null && calculateFCAIFactor(highestPriorityProcess) < calculateFCAIFactor(currentProcess)) {
                    // Preempt current process
                    int unusedQuantum = remainingQuantum - t; // Unused portion of quantum
                    currentProcess.setQuantum(currentProcess.getQuantum() + unusedQuantum);
                    readyQueue.add(currentProcess);
                    break;
                }

                // Continue executing current process
                currentProcess.setBurstTime(currentProcess.getBurstTime() - 1);
                currentTime++;

                if (currentProcess.getBurstTime() == 0) {
                    currentProcess.setCompletionTime(currentTime);
                    completedProcesses.add(currentProcess);
                    break;
                }
            }

            // If quantum ends without completing, increment quantum by 2
            if (currentProcess.getBurstTime() > 0 && currentProcess.getQuantum() == quantum) {
                currentProcess.setQuantum(currentProcess.getQuantum() + 2);
                readyQueue.add(currentProcess);
            }
        }

        calculateResults(completedProcesses, originalBurstTimes);
    }





    private void calculateResults(List<Process> completedProcesses, Map<String, Integer> originalBurstTimes) {
        int totalWaitingTime = 0;
        int totalTurnaroundTime = 0;

        for (Process process : completedProcesses) {
            int turnaroundTime = process.calculateTurnAroundTime();
            int waitingTime = turnaroundTime - originalBurstTimes.get(process.getName());
            process.setWaitingTime(waitingTime);

            totalWaitingTime += waitingTime;
            totalTurnaroundTime += turnaroundTime;

            // Print details for each process
            System.out.println("Process " + process.getName() + ":");
            System.out.println("Waiting Time: " + waitingTime);
            System.out.println("Turnaround Time: " + turnaroundTime);
            System.out.println();
        }

        System.out.println("Average Waiting Time: " + Math.ceil(totalWaitingTime / (double) completedProcesses.size()));
        System.out.println("Average Turnaround Time: " + Math.ceil(totalTurnaroundTime / (double) completedProcesses.size()));
    }

//    private void calculateResults(List<Process> completedProcesses) {
//        int totalWaitingTime = 0;
//        int totalTurnaroundTime = 0;
//
//        for (Process process : completedProcesses) {
//            int turnaroundTime = process.calculateTurnAroundTime();
//            int waitingTime = turnaroundTime - process.getBurstTime();
//            process.setWaitingTime(waitingTime);
//
//            totalWaitingTime += waitingTime;
//            totalTurnaroundTime += turnaroundTime;
//
//            // Print details for each process
//            System.out.println("Process " + process.getName() + ":");
//            System.out.println("Waiting Time: " + waitingTime);
//            System.out.println("Turnaround Time: " + turnaroundTime);
//            System.out.println();
//        }
//
//        System.out.println("Average Waiting Time: " + Math.ceil(totalWaitingTime / (double) completedProcesses.size()));
//        System.out.println("Average Turnaround Time: " + Math.ceil(totalTurnaroundTime / (double) completedProcesses.size()));
//    }
}
