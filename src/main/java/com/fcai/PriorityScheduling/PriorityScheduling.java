package com.fcai.PriorityScheduling;

import com.fcai.Main.GUIGraphNeeds;
import com.fcai.Main.GUIStatistics;
import com.fcai.Main.Scheduler;
import com.fcai.Main.Process;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PriorityScheduling extends Scheduler {
    public PriorityScheduling(List<Process> processList , int contextSwitchingTime) {
        this.processList = new ArrayList<>(processList);
        this.contextSwitchingTime = contextSwitchingTime;
    }

    @Override
    public void execute() {
        processList.sort(Comparator.comparingInt(Process::getArrivalTime));
        int currentTime = 0, counter = 0;
        for (Process process : processList) {
            process.setCompletionTime(0);
        }
        System.out.println("_____________________________________________________");
        System.out.println("Time     Process     Waiting Time     Turnaround Time");
        System.out.println("_____________________________________________________");

        List<Process> executedProcesses = new ArrayList<>();

        while (counter < processList.size()) {
            List<Process> readyProcessList = new ArrayList<>();
            for (Process process : processList) {
                if (process.getArrivalTime() > currentTime)
                    break;
                else if (process.getCompletionTime() != 0)
                    continue;
                else
                    readyProcessList.add(process);
            }

            if (!readyProcessList.isEmpty()) {
                readyProcessList.sort(Comparator.comparingInt(Process::getPriority).thenComparingInt(Process::getArrivalTime));
                Process currentProcess = readyProcessList.getFirst();
                
                guiGraphNeeds.add(new GUIGraphNeeds(currentProcess, currentTime, currentProcess.getBurstTime()));
    
                int completionTime = currentTime + currentProcess.getBurstTime();
                currentProcess.setCompletionTime(completionTime);
    
                int turnaroundTime = currentProcess.calculateTurnAroundTime();
    
                int waitingTime = turnaroundTime - currentProcess.getBurstTime();
                currentProcess.setWaitingTime(waitingTime);
    
                System.out.printf("%-9s%-12s%-17d%-15d%n", Integer.toString(currentTime) + '-' + completionTime, currentProcess.getName(), waitingTime, turnaroundTime);
                System.out.println("_____________________________________________________");
    
                int afterCSTime = completionTime + contextSwitchingTime;
                System.out.printf("%-9sContext Switching%n", Integer.toString(completionTime) + '-' + afterCSTime);
                System.out.println("_____________________________________________________");
    
                currentTime = afterCSTime;
                counter++;
                executedProcesses.add(currentProcess);
            }
            else {
                currentTime++;
            }
        }

//        System.out.print('\n');
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

        guiStatistics=new GUIStatistics("Priority Scheduling",avWaitingTime , avTurnaroundTime);
    }
}
