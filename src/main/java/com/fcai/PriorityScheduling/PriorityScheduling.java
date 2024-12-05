package com.fcai.PriorityScheduling;

import com.fcai.Main.GUIGraphNeeds;
import com.fcai.Main.Scheduler;
import com.fcai.Main.Process;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PriorityScheduling extends Scheduler {
    public PriorityScheduling(List<Process> processList) {
        this.processList = new ArrayList<>(processList);
        this.contextSwitchingTime = 1;
    }

    @Override
    public void execute() {
        processList.sort(Comparator.comparingInt(Process::getArrivalTime));
        int currentTime = contextSwitchingTime;
        for (Process process : processList) {
            process.setCompletionTime(0);
        }
        System.out.println("_____________________________________________________");
        System.out.println("Time     Process     Waiting Time     Turnaround Time");
        System.out.println("_____________________________________________________");

        for (int i = 0; i < processList.size(); i++) {
            List<Process> readyProcessList = new ArrayList<>();
            for (Process process : processList) {
                if (process.getArrivalTime() > currentTime - contextSwitchingTime)
                    break;
                else if (process.getCompletionTime() != 0)
                    continue;
                else
                    readyProcessList.add(process);
            }
            readyProcessList.sort(Comparator.comparingInt(Process::getPriority).thenComparingInt(Process::getArrivalTime));
            Process currentProcess = readyProcessList.getFirst();

            if (i == 0)
                currentTime -= contextSwitchingTime;
            
            guiGraphNeeds.add(new GUIGraphNeeds(currentProcess, currentTime, currentProcess.getBurstTime()));

            int completionTime = currentTime + currentProcess.getBurstTime();
            currentProcess.setCompletionTime(completionTime);

            int turnaroundTime = currentProcess.calculateTurnAroundTime();

            int waitingTime = turnaroundTime - currentProcess.getBurstTime();
            currentProcess.setWaitingTime(waitingTime);

            System.out.printf("%-9s%-12s%-17d%-15d%n", Integer.toString(currentTime) + '-' + completionTime, currentProcess.getName(), waitingTime, turnaroundTime);
            System.out.println("_____________________________________________________");

            int afterCSTime = completionTime + this.contextSwitchingTime;
            System.out.printf("%-9sContext Switching%n", Integer.toString(completionTime) + '-' + afterCSTime);
            System.out.println("_____________________________________________________");

            currentTime = afterCSTime;
        }

        System.out.print('\n');
        int averageWaitingTime = calculateAverageWaitingTime();
        int averageTurnAroundTime = calculateAverageTurnAroundTime();
        System.out.println("Average Waiting Time: " + averageWaitingTime);
        System.out.println("Average Turn Around Time: " + averageTurnAroundTime);
    }
}
