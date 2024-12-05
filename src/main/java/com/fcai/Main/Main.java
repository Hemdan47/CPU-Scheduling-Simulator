package com.fcai.Main;

import com.fcai.FCAI.FCAI;
import com.fcai.PriorityScheduling.PriorityScheduling;
import com.fcai.SRTF.SRTF;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;


import java.util.List;
import java.util.Objects;

public class Main extends Application {
    private static final int PROCESS_HEIGHT = 45;
    private static final int BURST_WIDTH = 25;
    private static final int startX = 50;
    private static final int startY = 50;

    private int totalRunningDuration = 0;

    private GraphicsContext gc;

    Process p1 = new Process("p1", Color.LIGHTBLUE, 17, 0, 4, 4);
    Process p2 = new Process("p2", Color.LIGHTSALMON, 6, 3, 9, 3);
    Process p3 = new Process("p3", Color.LIGHTPINK, 10, 4, 3, 5);
    Process p4 = new Process("p4", Color.LIGHTGREEN, 4, 29, 2, 2);
    List<Process> processList = List.of(p1, p2, p3, p4);

    Scheduler s = new FCAI(processList);

    @Override
    public void start(Stage primaryStage) {

        Canvas canvas = new Canvas(2000, 1000);
        gc = canvas.getGraphicsContext2D();

        drawStaticParts();
        s.execute();

        //Animation
        AnimationTimer animationTimer = new AnimationTimer() {

            long lastUpdate = 0;
            int index = 0;
            int animationProgress = 1;
            Process nextProcess = s.guiGraphNeeds.get(1).getProcess();

            @Override
            public void handle(long now) {
                if (now - lastUpdate >= 0.15e9) {
                    if (index < s.guiGraphNeeds.size()) {
                        GUIGraphNeeds currentNeeds = s.guiGraphNeeds.get(index);

                        Process process = currentNeeds.getProcess();
                        int currentTime = currentNeeds.getCurrentTime();
                        int runningDuration = currentNeeds.getRunningDuration();

                        boolean isBurstFinish = ((animationProgress == runningDuration) && (!Objects.equals(process.getName(), nextProcess.getName()) || index == s.guiGraphNeeds.size() - 1));

                        drawDynamicParts(process, currentTime + animationProgress - 1, 1, isBurstFinish);

                        if (animationProgress == runningDuration) {
                            index++;
                            if (index < s.guiGraphNeeds.size() - 1)
                                nextProcess = s.guiGraphNeeds.get(index + 1).getProcess();

                            animationProgress = 0;
                        }
                        animationProgress++;

                        lastUpdate = now;
                    } else {
                        stop();
                    }
                }

            }
        };

        animationTimer.start();

        // Scene amd Stage set up
        BorderPane root = new BorderPane();

        //set title
        Text simulatorTitle = new Text("CPU Scheduling Simulator");
        simulatorTitle.setFill(Color.RED);
        root.setTop(simulatorTitle);

        //set Animation
        ScrollPane graph = new ScrollPane(canvas);

        graph.setPannable(true); // Allow dragging the viewport with the mouse
        graph.setFitToWidth(true); // Adjust horizontal scrollbar behavior
        graph.setFitToHeight(true);// Adjust vertical scrollbar behavior

        root.setCenter(graph);

        //set Statistics
//        VBox statistics=new VBox();
//
//        //statistics title
//        Text statisticsText = new Text("Statistics");
//        statisticsText.setFill(Color.RED);
//        statistics.getChildren().add(statisticsText);
//
//        //statistics table
//        statistics.getChildren().add(setStatisticsTable());
//        statistics.setSpacing(20);
//        statistics.setAlignment(Pos.TOP_CENTER);
//        //root.setBottom(statistics);


        Scene scene = new Scene(root, 800, 400);

        primaryStage.setTitle("CPU Scheduling Graph");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    void drawStaticParts() {
        gc.setFill(Color.BLACK);

        //labels
        for (int i = 0; i < processList.size(); i++) {
            gc.fillText(processList.get(i).getName(), startX, calcProcessPosition(i));
        }
    }

    public void drawDynamicParts(Process currentProcess, int currentTime, int runningDuration, boolean isBurstFinish) {
        gc.setFill(currentProcess.getColor());

        int distantX = currentTime * BURST_WIDTH;
        if (distantX == 0) distantX = BURST_WIDTH;

        int processPosition = calcProcessPosition(processList.indexOf(currentProcess));

        gc.fillRect(
                startX + distantX,
                processPosition - (PROCESS_HEIGHT / 2),
                runningDuration * BURST_WIDTH,
                PROCESS_HEIGHT
        );

        if (isBurstFinish) {
            gc.setFill(Color.BLACK);
            totalRunningDuration += runningDuration;
            String durationText = totalRunningDuration + " unit time";
            System.out.println(durationText);
            gc.fillText(durationText, startX + distantX - (totalRunningDuration * BURST_WIDTH) * 0.4, processPosition);

            totalRunningDuration = 0;
        } else {
            totalRunningDuration += runningDuration;
        }
    }

    private int calcProcessPosition(int index) {
        return (startY + ((index) * (PROCESS_HEIGHT * 2)));
    }

    private GridPane setStatisticsTable() {
        GridPane statisticsTable = new GridPane();
        GUIStatistics guiStatistics = s.guiStatistics;

        Text scheduleTitle = new Text("Schedule Name: ");
        statisticsTable.add(scheduleTitle, 0, 0);
        Text scheduleName = new Text(guiStatistics.getScheduleName());
        statisticsTable.add(scheduleName, 1, 0);

        Text avWaitingTimeTitle = new Text("Average Waiting Time: ");
        statisticsTable.add(avWaitingTimeTitle, 0, 1);
        Text avWaitingTime = new Text(String.valueOf(guiStatistics.getAvWaitingTime()));
        statisticsTable.add(avWaitingTime, 1, 1);

        Text avTurnaroundTimeTitle = new Text("Average Turnaround Time: ");
        statisticsTable.add(avTurnaroundTimeTitle, 0, 2);
        Text avTurnaroundTime = new Text(String.valueOf(guiStatistics.getAvTurnaroundTime()));
        statisticsTable.add(avTurnaroundTime, 1, 2);

        return statisticsTable;
    }


    public static void main(String[] args) {
        launch(args);
    }
}