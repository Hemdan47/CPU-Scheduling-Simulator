package com.fcai.Main;

import com.fcai.FCAI.FCAI;
import com.fcai.PriorityScheduling.PriorityScheduling;
import com.fcai.SJF.SJF;
import com.fcai.SRTF.SRTF;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Main extends Application {




    @Override
    public void start(Stage stage) throws IOException {

        Parent root = FXMLLoader.load(getClass().getResource("Main-View.fxml"));

        Scene scene = new Scene(root);

        stage.setTitle("Cpu Scheduling Simulator");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

    }




    public static void main(String[] args) {
        launch(args);
    }
}