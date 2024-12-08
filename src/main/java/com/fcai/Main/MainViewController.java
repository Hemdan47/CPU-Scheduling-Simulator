package com.fcai.Main;

import com.fcai.FCAI.FCAI;
import com.fcai.PriorityScheduling.PriorityScheduling;
import com.fcai.SJF.SJF;
import com.fcai.SJF.SJFstarvation;
import com.fcai.SRTF.SRTF;
import com.fcai.SRTF.SRTFstarvation;
import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainViewController {
    private static final int PROCESS_HEIGHT = 45;
    private static final int BURST_WIDTH = 25;
    private static final int startX = 50;
    private static final int startY = 50;

    private int totalRunningDuration = 0;

    private GraphicsContext gc;
    Scheduler s;

    @FXML
    private ListView<Process> listView;
    @FXML
    private Button executeBtn;
    @FXML
    private Button addProcessButton;
    @FXML
    private TextField processName;
    //    @FXML
//    private TextField processColorR;
//    @FXML
//    private TextField processColorG;
//    @FXML
//    private TextField processColorB;
    @FXML
    private ColorPicker processColorPicker;

    @FXML
    private TextField burstTime;
    @FXML
    private TextField arrivalTime;
    @FXML
    private TextField priority;
    @FXML
    private TextField quantum;
    @FXML
    private TextField contextSwitching;
    @FXML
    private TextField threshold;
    @FXML
    private Label contextSwitchingLabel;
    @FXML
    private Label thresholdLabel;
    @FXML
    private RadioButton sjf;
    @FXML
    private RadioButton ps;
    @FXML
    private RadioButton fcai;
    @FXML
    private RadioButton srtf;
    @FXML
    private RadioButton srtfStarvation;
    @FXML
    private RadioButton sjfStarvation;

    @FXML
    public void initialize() {
        // Set the default visibility when the scene loads
        updateVisibility(sjf); // Assume 'sjf' is selected initially

        // Add listeners for the radio buttons
        sjf.setOnAction(event -> updateVisibility(sjf));
        ps.setOnAction(event -> updateVisibility(ps));
        fcai.setOnAction(event -> updateVisibility(fcai));
        srtf.setOnAction(event -> updateVisibility(srtf));
        srtfStarvation.setOnAction(event -> updateVisibility(srtfStarvation));
        sjfStarvation.setOnAction(event -> updateVisibility(sjfStarvation));
    }

    private void updateVisibility(RadioButton selectedRadioButton) {
        contextSwitching.setVisible(true);
        threshold.setVisible(true);
        contextSwitchingLabel.setVisible(true);
        thresholdLabel.setVisible(true);

        if (selectedRadioButton == sjf || selectedRadioButton == fcai) {
            threshold.setVisible(false);
            thresholdLabel.setVisible(false);
            contextSwitching.setVisible(false);
            contextSwitchingLabel.setVisible(false);
        } else if (selectedRadioButton== ps || selectedRadioButton==srtf) {
            threshold.setVisible(false);
            thresholdLabel.setVisible(false);
        } else if (selectedRadioButton==sjfStarvation) {
            contextSwitching.setVisible(false);
            contextSwitchingLabel.setVisible(false);
        }
    }

private ArrayList<Process> processList;

public MainViewController() {
    processList = new ArrayList<>();
}

public void addProcess(ActionEvent event) {
    String name = processName.getText();
    Color color = processColorPicker.getValue();

    int burst = Integer.parseInt(burstTime.getText());
    int arrival = Integer.parseInt(arrivalTime.getText());
    int processPriority = Integer.parseInt(priority.getText());
    int processQuantum = Integer.parseInt(quantum.getText());
    Process process = new Process(name, color, burst, arrival, processPriority, processQuantum);
    processList.add(process);
    listView.getItems().add(process);
}


public void execute(ActionEvent event) {

    if (sjf.isSelected()) {
        s = new SJF(processList);
    } else if (ps.isSelected()) {
        int contextSwitchingValue = Integer.parseInt(contextSwitching.getText());
        s = new PriorityScheduling(processList, contextSwitchingValue);
    } else if (fcai.isSelected()) {
        s = new FCAI(processList);
    } else if (srtf.isSelected()) {
        int contextSwitchingValue = Integer.parseInt(contextSwitching.getText());
        s = new SRTF(processList, contextSwitchingValue);
    } else if (srtfStarvation.isSelected()) {
        int contextSwitchingValue = Integer.parseInt(contextSwitching.getText());
        int thresholdValue = Integer.parseInt(threshold.getText());
        s = new SRTFstarvation(processList, contextSwitchingValue, thresholdValue);
    } else if (sjfStarvation.isSelected()) {
        int thresholdValue = Integer.parseInt(threshold.getText());
        s = new SJFstarvation(processList, thresholdValue);
    }


    /// /////////////////////////////////////////////////////
    /// ////////////////////////////////////////////////////

    // Get the current stage
    Stage primaryStage = (Stage) ((Button) event.getSource()).getScene().getWindow();
    Canvas canvas = new Canvas(2000, 1000);
    gc = canvas.getGraphicsContext2D();


        s.execute();
        drawStaticParts();

    //Animation
    AnimationTimer animationTimer = new AnimationTimer() {

        long lastUpdate = 0;
        int index = 0;
        int animationProgress = 1;

        Process nextProcess = (s.guiGraphNeeds.size() > 1)? s.guiGraphNeeds.get(1).getProcess() : null;

        @Override
        public void handle(long now) {
            if (now - lastUpdate >= 0.15e9) {
                if (index < s.guiGraphNeeds.size()) {
                    GUIGraphNeeds currentNeeds = s.guiGraphNeeds.get(index);

                    Process process = currentNeeds.getProcess();
                    int currentTime = currentNeeds.getCurrentTime();
                    int runningDuration = currentNeeds.getRunningDuration();

                    boolean isBurstFinish = (nextProcess != null && (animationProgress == runningDuration) && (!Objects.equals(process.getName(), nextProcess.getName()) || index == s.guiGraphNeeds.size() - 1));

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

    ScrollPane graph = new ScrollPane(canvas);

    graph.setPannable(true); // Allow dragging the viewport with the mouse
    graph.setFitToWidth(true); // Adjust horizontal scrollbar behavior
    graph.setFitToHeight(true);// Adjust vertical scrollbar behavior

    root.setCenter(graph);

    //set Statistics
    VBox statistics = new VBox();

    //statistics title
    Text statisticsText = new Text("Statistics");
    statisticsText.setFill(Color.RED);
    statistics.getChildren().add(statisticsText);

    //statistics table
    statistics.getChildren().add(setStatisticsTable());
    statistics.setSpacing(20);
    statistics.setAlignment(Pos.TOP_CENTER);
    root.setBottom(statistics);

    //set process info
    VBox processesInfo = new VBox();

    //processInfo title
    Text processInfoText = new Text("Process Information Table");
    processInfoText.setFill(Color.RED);
    processesInfo.getChildren().add(processInfoText);

    //ProcessInfo Table
    processesInfo.getChildren().add(setProcessesInfoTable());
    processesInfo.setSpacing(20);
    processesInfo.setAlignment(Pos.TOP_CENTER);
    root.setTop(processesInfo);

    Scene scene = new Scene(root, 900, 600);
    scene.setFill(Color.BLUE);

    primaryStage.setTitle("CPU Scheduling Graph");
    primaryStage.setScene(scene);
    primaryStage.setResizable(true);
    primaryStage.show();

}


void drawStaticParts() {

        int xLength = calculateXLength();

    //labels
    for (int i = 0; i < processList.size(); i++) {
        gc.setFill(Color.BLACK);
        int y = calcProcessPosition(i);
        gc.fillText(processList.get(i).getName(), startX, y);


            gc.setStroke(Color.GRAY);
            gc.setLineDashes(5);
            gc.strokeLine(startX + BURST_WIDTH, y, startX + (xLength) * BURST_WIDTH, y);
            gc.setLineDashes(0);
        }

        int processPosition = calcProcessPosition(processList.size());
        for (int i = 0; i <= xLength; i++) {
            gc.setFill(Color.BLACK);
            int x = startX + (BURST_WIDTH * (i + 1));

        gc.fillText(String.valueOf(i), x - 5, processPosition - (PROCESS_HEIGHT * 0.75) + 10);

        gc.setStroke(Color.GRAY);
        gc.setLineDashes(5);
        gc.strokeLine(x, 10, x, processPosition - (PROCESS_HEIGHT * 0.75) - 5);
    }
}

    int calculateXLength() {

        GUIGraphNeeds lastBurst=s.guiGraphNeeds.getLast();

       return lastBurst.getCurrentTime()+lastBurst.getRunningDuration()+10;
    }

public void drawDynamicParts(Process currentProcess, int currentTime, int runningDuration, boolean isBurstFinish) {
    gc.setFill(currentProcess.getColor());

    int distantX = (currentTime + 1) * BURST_WIDTH;
    //if (distantX <= 0) distantX = BURST_WIDTH;

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
    statisticsTable.setPadding(new Insets(10, 20, 10, 20)); // top, right, bottom, left

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

    statisticsTable.setHgap(15.0);
    statisticsTable.setVgap(10);

    return statisticsTable;
}

private GridPane setProcessesInfoTable() {
    GridPane processesInfoTable = new GridPane();

    String[] columnTitles = new String[]{
            "Order", "Name", "Color", "Priority", "Quantum", "Burst Time", "Arrival Time", "Waiting Time", "Turnaround Time", "Complete Time"
    };

    List<Process> processList = s.processList;

    for (int i = 0; i < columnTitles.length; i++) {
        Text columnTitle = new Text(columnTitles[i]);
        columnTitle.setFill(Color.RED);

        processesInfoTable.add(columnTitle, i, 0);
    }

    for (int i = 0; i < processList.size(); i++) {
        Text id = new Text(String.valueOf(i + 1));
        processesInfoTable.add(id, 0, i + 1);

        Text name = new Text(processList.get(i).getName());
        processesInfoTable.add(name, 1, i + 1);
        Rectangle color = new Rectangle();
        color.setFill(processList.get(i).getColor());
        color.setHeight(20);
        color.setWidth(30);
        processesInfoTable.add(color, 2, i + 1);

        Text priority = new Text(String.valueOf(processList.get(i).getPriority()));
        processesInfoTable.add(priority, 3, i + 1);

        Text quantum = new Text(String.valueOf(processList.get(i).getQuantum()));
        processesInfoTable.add(quantum, 4, i + 1);

        Text burstTime = new Text(String.valueOf(processList.get(i).getInitialBurstTime()));
        processesInfoTable.add(burstTime, 5, i + 1);

        Text arrivalTime = new Text(String.valueOf(processList.get(i).getArrivalTime()));
        processesInfoTable.add(arrivalTime, 6, i + 1);

        Text waitingTime = new Text(String.valueOf(processList.get(i).getWaitingTime()));
        processesInfoTable.add(waitingTime, 7, i + 1);

        int turnaroundTime = processList.get(i).getCompletionTime() - processList.get(i).getArrivalTime();
        Text turnaroundTimeText = new Text(String.valueOf(turnaroundTime));
        processesInfoTable.add(turnaroundTimeText, 8, i + 1);

        Text completionTime = new Text(String.valueOf(processList.get(i).getCompletionTime()));
        processesInfoTable.add(completionTime, 9, i + 1);
    }

    processesInfoTable.setAlignment(Pos.TOP_CENTER);
    processesInfoTable.setHgap(15.0);
    processesInfoTable.setVgap(10);

    processesInfoTable.setStyle("-fx-border: 3px solid; -fx-border-color: red;");

    return processesInfoTable;
}
}
