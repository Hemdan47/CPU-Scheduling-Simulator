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


    private Process getHighestPriority(List<Process> processes) {
        Process highestPriorityProcess = null;
        for(Process process : processes){
            if(highestPriorityProcess == null){
                highestPriorityProcess = process;
            }
            if(calculateFCAIFactor(process) < calculateFCAIFactor(highestPriorityProcess)){
                highestPriorityProcess = process;
            }
        }
        return highestPriorityProcess;
    }



//    @Override
//    public HashMap<String , ArrayList<Duration>> execute() {
//        HashMap<String , ArrayList<Duration>> durMap = new HashMap<>();
//        PriorityQueue<Process> readyQueue = new PriorityQueue<>(
//                Comparator.comparingDouble(this::calculateFCAIFactor)
//        );
//        List<Process> waitingQueue = new ArrayList<>();
//
//        int currentTime = 0;
//        List<Process> completedProcesses = new ArrayList<>();
//        Map<String, Integer> originalBurstTimes = new HashMap<>();
//        for (Process process : processList) {
//            originalBurstTimes.put(process.getName(), process.getBurstTime());
//        }
//
//        while (!processList.isEmpty() || !readyQueue.isEmpty() || !waitingQueue.isEmpty()) {
//            // Move arrived processes to the ready queue
////            Iterator<Process> it = processList.iterator();
////            while (it.hasNext()) {
////                Process process = it.next();
////                if (process.getArrivalTime() <= currentTime) {
////                    readyQueue.add(process);
////                    it.remove();
////                }
////            }
//            for (int i = 0; i < processList.size() ; i++) {
//                if(processList.get(i).getArrivalTime() <= currentTime) {
//                    readyQueue.add(processList.get(i));
//                    processList.remove(i);
//                }
//            }
//
//            if (readyQueue.isEmpty() && waitingQueue.isEmpty()) {
//                currentTime++; // Increment time if no process is ready
//                continue;
//            }
//
//            Process currentProcess = readyQueue.poll();
//            readyQueue.addAll(waitingQueue);
//            waitingQueue.clear();
//            if(currentProcess== null){
//                currentProcess = readyQueue.poll();
//            }
//            Duration duration = new Duration(currentTime);
//
////            System.out.println(currentProcess.getName() + ":  " + calculateFCAIFactor(currentProcess));
//            int quantum = currentProcess.getQuantum();
//            int first40Percent = (int) Math.ceil(0.4 * quantum);
//            int remainingQuantum = quantum - first40Percent;
//
//            // Execute the first 40% non-preemptively
//            int executionTime = Math.min(first40Percent, currentProcess.getBurstTime());
//            currentProcess.setBurstTime(currentProcess.getBurstTime() - executionTime);
//            currentTime += executionTime;
//
//            // If the process finishes during the first 40%, update completion time
//            if (currentProcess.getBurstTime() == 0) {
//                currentProcess.setCompletionTime(currentTime);
//                completedProcesses.add(currentProcess);
//                duration.setEnd(currentTime);
//                addDuration(currentProcess, duration, durMap);
//                continue;
//            }
//
//            // Preemptive execution for the remaining quantum
//            for (int t = 0; t < remainingQuantum; t++) {
//                // Add newly arrived processes to the queue
//
//
    ////                Iterator<Process> newIt = processList.iterator();
    ////                while (newIt.hasNext()) {
    ////                    Process process = newIt.next();
    ////                    if (process.getArrivalTime() <= currentTime) {
    ////                        readyQueue.add(process);
    ////                        newIt.remove();
    ////                    }
    ////                }
//                for (int i = 0; i < processList.size() ; i++) {
//                    if(processList.get(i).getArrivalTime() <= currentTime) {
//                        readyQueue.add(processList.get(i));
//                        processList.remove(i);
//                    }
//                }
//
//                // Check for higher-priority process
//                Process highestPriorityProcess = readyQueue.peek();
//                if (highestPriorityProcess != null && calculateFCAIFactor(highestPriorityProcess) < calculateFCAIFactor(currentProcess)) {
//                    // Preempt current process
//                    int unusedQuantum = remainingQuantum - t; // Unused portion of quantum
//                    currentProcess.setQuantum(currentProcess.getQuantum() + unusedQuantum);
//                    readyQueue.add(currentProcess);
//                    duration.setEnd(currentTime);
//                    addDuration(currentProcess, duration, durMap);
//                    break;
//                }
//
//                // Continue executing current process
//                currentProcess.setBurstTime(currentProcess.getBurstTime() - 1);
//                currentTime++;
//
//                if (currentProcess.getBurstTime() == 0) {
//                    currentProcess.setCompletionTime(currentTime);
//                    completedProcesses.add(currentProcess);
//                    duration.setEnd(currentTime);
//                    addDuration(currentProcess, duration, durMap);
//                    break;
//                }
//            }
//
//            // If quantum ends without completing, increment quantum by 2
//            if (currentProcess.getBurstTime() > 0 && currentProcess.getQuantum() == quantum) {
//                currentProcess.setQuantum(currentProcess.getQuantum() + 2);
//                waitingQueue.add(currentProcess);
//                duration.setEnd(currentTime);
//                addDuration(currentProcess, duration, durMap);
//            }
//
//        }
//        return durMap;
//    }





//    //v2

    @Override
    public void execute() {
        List<Process> readyQueue = new ArrayList<>();

        int currentTime = 0;
        List<Process> completedProcesses = new ArrayList<>();
        Map<String, Integer> initialBurstTimeMap = new HashMap<>();
        for (Process process : processList) {
            initialBurstTimeMap.put(process.getName(), process.getBurstTime());
        }

        while (!processList.isEmpty() || !readyQueue.isEmpty()) {

            // Move arrived processes to the ready queue
            for (int i = 0; i < processList.size() ; i++) {
                if(processList.get(i).getArrivalTime() <= currentTime) {
                    readyQueue.add(processList.get(i));
                    processList.remove(i);
                }
            }

            if (readyQueue.isEmpty()) {
                currentTime++; // Increment time if no process is ready
                continue;
            }

            Process currentProcess = readyQueue.get(0);
            readyQueue.remove(0);
            double currentProcessFcaiFactor = calculateFCAIFactor(currentProcess);

            int quantum = currentProcess.getQuantum();
            int first40Percent = (int) Math.ceil(0.4 * quantum);
            int remainingQuantum = quantum - first40Percent;

            // Execute the first 40% non-preemptively
            int executionTime = Math.min(first40Percent, currentProcess.getBurstTime());
            currentProcess.setBurstTime(currentProcess.getBurstTime() - executionTime);
            for (int i = 0; i < executionTime; i++) {
                guiGraphNeeds.add(new GUIGraphNeeds(currentProcess, currentTime + i+1, 1));
            }
            currentTime += executionTime;

            // If the process finishes during the first 40%, update completion time
            if (currentProcess.getBurstTime() == 0) {
                currentProcess.setCompletionTime(currentTime);
                currentProcess.setWaitingTime(currentProcess.calculateTurnAroundTime()-initialBurstTimeMap.get(currentProcess.getName()));
                completedProcesses.add(currentProcess);
                continue;
            }


            // Preemptive execution for the remaining quantum
            for (int t = 0; t < remainingQuantum; t++) {

                // Add newly arrived processes to the queue
                for (int i = 0; i < processList.size() ; i++) {
                    if(processList.get(i).getArrivalTime() <= currentTime) {
                        readyQueue.add(processList.get(i));
                        processList.remove(i);
                    }
                }

                // Check for higher-priority process
                Process highestPriorityProcess = getHighestPriority(readyQueue);


                if (highestPriorityProcess != null && (calculateFCAIFactor(highestPriorityProcess) < currentProcessFcaiFactor)) {
                    // Preempt current process
                    int unusedQuantum = remainingQuantum - t; // Unused portion of quantum
                    currentProcess.setQuantum(currentProcess.getQuantum() + unusedQuantum);
                    readyQueue.add(currentProcess);
                    readyQueue.remove(highestPriorityProcess);
                    readyQueue.add(0 , highestPriorityProcess);
                    break;
                }

                // Continue executing current process
                currentProcess.setBurstTime(currentProcess.getBurstTime() - 1);
                guiGraphNeeds.add(new GUIGraphNeeds(currentProcess, currentTime +1, 1));
                currentTime++;

                if (currentProcess.getBurstTime() == 0) {
                    currentProcess.setCompletionTime(currentTime);
                    currentProcess.setWaitingTime(currentProcess.calculateTurnAroundTime()-initialBurstTimeMap.get(currentProcess.getName()));
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

        processList = completedProcesses;
        for (Process p : processList) {
            System.out.printf("Process: %s, Waiting Time: %d, Turnaround Time: %d\n",
                    p.getName(), p.getWaitingTime(), p.calculateTurnAroundTime());
        }

        double avWaitingTime=calculateAverageWaitingTime();
        double avTurnaroundTime=calculateAverageTurnAroundTime();

        System.out.printf("Average Waiting Time: %.2f%n", avWaitingTime);
        System.out.printf("Average Turnaround Time: %.2f%n", avTurnaroundTime);

        guiStatistics=new GUIStatistics("SRTF",avWaitingTime , avTurnaroundTime);

    }



}
