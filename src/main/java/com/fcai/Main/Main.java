package com.fcai.Main;
import com.fcai.FCAI.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Text t = new Text("Hello World");
        StackPane pane = new StackPane(t);
        Scene scene = new Scene(pane, 320, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
//        launch();

        Process p1 = new Process("p1" , "first" , 17 , 0 , 4 , 4);
        Process p2 = new Process("p2" , "second" , 6 , 3 , 9 , 3);
        Process p3 = new Process("p3" , "third" , 10 , 4 , 3 , 5);
        Process p4 = new Process("p4" , "fourth" , 4 , 29 , 2 , 2);
        List<Process> list = List.of(p1,p2,p3,p4);
        Scheduler s = new FCAI(list);
        s.addProcess(p1);
        s.addProcess(p2);
        s.addProcess(p3);
        s.addProcess(p4);
        s.execute();
    }
}