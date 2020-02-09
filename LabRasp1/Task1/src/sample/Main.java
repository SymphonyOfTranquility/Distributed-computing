package sample;

import javafx.application.Application;
import javafx.scene.control.Slider;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import static java.lang.Integer.max;
import static java.lang.Integer.min;

public class Main extends Application{

    private int priorityTThread1 = 1;
    private int priorityTThread2 = 1;

    private Slider slider = new Slider(0.0, 100.0, 50.0);
    private SliderThread TThread1 = new SliderThread(slider, 10, -1);
    private SliderThread TThread2 = new SliderThread(slider, 90, 1);

    public static void main(String[] args) {

        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {

        slider.setShowTickMarks(true);
        slider.setShowTickLabels(true);
        slider.setBlockIncrement(2.0);
        slider.setMajorTickUnit(10.0);
        slider.setMinorTickCount(4);
        slider.setSnapToTicks(true);
        slider.setPrefSize(350, 10);
        slider.setLayoutX(60);
        slider.setLayoutY(50);

        Button startButton = new Button("Start");
        startButton.setLayoutX(65);
        startButton.setLayoutY(20);
        startButton.setPrefSize(340, 20);
        startButton.setOnAction(event -> {
            TThread1.setPriority(priorityTThread1);
            TThread2.setPriority(priorityTThread2);

            TThread1.start();
            TThread2.start();

            startButton.setDisable(true);
        });

        Label priority1Label = new Label("1");
        priority1Label.setLayoutX(35);
        priority1Label.setLayoutY(70);
        Button upPriority1 = new Button("↑");
        upPriority1.setLayoutX(20);
        upPriority1.setLayoutY(20);
        upPriority1.setPrefSize(40, 20);
        upPriority1.setOnAction(event -> {
            priorityTThread1 = min(priorityTThread1+1, 10);
            TThread1.setPriority(priorityTThread1);
            System.out.println(TThread1.getPriority());
            priority1Label.setText(String.valueOf(priorityTThread1));
        });
        Button downPriority1 = new Button("↓");
        downPriority1.setLayoutX(20);
        downPriority1.setLayoutY(45);
        downPriority1.setPrefSize(40, 20);
        downPriority1.setOnAction(event -> {
            priorityTThread1 = max(priorityTThread1-1, 1);
            TThread1.setPriority(priorityTThread1);
            System.out.println(TThread1.getPriority());
            priority1Label.setText(String.valueOf(priorityTThread1));
        });

        Label priority2Label = new Label("1");
        priority2Label.setLayoutX(425);
        priority2Label.setLayoutY(70);
        Button upPriority2 = new Button("↑");
        upPriority2.setLayoutX(410);
        upPriority2.setLayoutY(20);
        upPriority2.setPrefSize(40, 20);
        upPriority2.setOnAction(event -> {
            priorityTThread2 = min(priorityTThread2+1, 10);
            TThread2.setPriority(priorityTThread2);
            System.out.println(TThread2.getPriority());
            priority2Label.setText(String.valueOf(priorityTThread2));
        });
        Button downPriority2 = new Button("↓");
        downPriority2.setLayoutX(410);
        downPriority2.setLayoutY(45);
        downPriority2.setPrefSize(40, 20);
        downPriority2.setOnAction(event -> {
            priorityTThread2 = max(priorityTThread2-1, 1);
            TThread2.setPriority(priorityTThread2);
            System.out.println(TThread2.getPriority());
            priority2Label.setText(String.valueOf(priorityTThread2));
        });

        Group group = new Group(slider, startButton, upPriority1, downPriority1, priority1Label,
                upPriority2, downPriority2, priority2Label);

        Scene scene = new Scene(group, 470, 100);
        stage.setScene(scene);

        stage.setTitle("Task1 a");

        stage.show();
    }

    @Override
    public void stop()
    {
        TThread1.interrupt();
        TThread2.interrupt();
    }
}