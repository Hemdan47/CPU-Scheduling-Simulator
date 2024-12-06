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
            }
            else {
                currentTime++;
            }
        }

        System.out.print('\n');
        double avWaitingTime=calculateAverageWaitingTime();
        double avTurnaroundTime=calculateAverageTurnAroundTime();

        System.out.printf("Average Waiting Time: %.2f%n", avWaitingTime);
        System.out.printf("Average Turnaround Time: %.2f%n", avTurnaroundTime);

        guiStatistics=new GUIStatistics("Priority Scheduling",avWaitingTime , avTurnaroundTime);
    }
}
