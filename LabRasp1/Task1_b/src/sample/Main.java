package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Slider;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import static java.lang.Integer.max;
import static java.lang.Integer.min;

public class Main extends Application{

    private int semaphore = 0;

    private Slider slider = new Slider(0.0, 100.0, 50.0);
    private SliderThread TThread1 = new SliderThread(slider, 10, -1);
    private SliderThread TThread2 = new SliderThread(slider, 90, 1);

    public static void main(String[] args) {

        Application.launch(args);
    }

    void showAlert()
    {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning!!!");
        alert.setHeaderText(null);
        alert.setContentText("Semaphore is locked");
        Platform.runLater(() -> {
            alert.getDialogPane().getScene().getWindow().sizeToScene();
        });
        alert.showAndWait();

    }

    @Override
    public void start(Stage stage) {

        TThread1.start();
        TThread2.start();
        slider.setShowTickMarks(true);
        slider.setShowTickLabels(true);
        slider.setBlockIncrement(2.0);
        slider.setMajorTickUnit(10.0);
        slider.setMinorTickCount(4);
        slider.setSnapToTicks(true);
        slider.setPrefSize(350, 10);
        slider.setLayoutX(105);
        slider.setLayoutY(30);


        Button buttonStart1 = new Button("Start 1");
        Button endButton1 = new Button("End 1");
        Button buttonStart2 = new Button("Start 2");
        Button endButton2 = new Button("End 2");

        buttonStart1.setLayoutX(20);
        buttonStart1.setLayoutY(20);
        buttonStart1.setPrefSize(80, 20);
        buttonStart1.setOnAction(event -> {
            if (semaphore == 0) {
                semaphore = 1;
                TThread1.setPriority(Thread.MIN_PRIORITY);
                TThread1.startRunning();

                endButton2.setDisable(true);
            }
            else {
                showAlert();
            }
        });
        endButton1.setLayoutX(20);
        endButton1.setLayoutY(45);
        endButton1.setPrefSize(80, 20);
        endButton1.setOnAction(event -> {
            TThread1.stopRunning();
            semaphore = 0;
            endButton2.setDisable(false);
        });

        buttonStart2.setLayoutX(460);
        buttonStart2.setLayoutY(20);
        buttonStart2.setPrefSize(80, 20);
        buttonStart2.setOnAction(event -> {
            if (semaphore == 0) {
                semaphore = 1;
                TThread2.setPriority(Thread.MAX_PRIORITY);
                TThread2.startRunning();
                endButton1.setDisable(true);
            }
            else {
                showAlert();
            }
        });
        endButton2.setLayoutX(460);
        endButton2.setLayoutY(45);
        endButton2.setPrefSize(80, 20);
        endButton2.setOnAction(event -> {
            TThread2.stopRunning();
            semaphore = 0;
            endButton1.setDisable(false);
        });

        Group group = new Group(slider, buttonStart1, endButton1, buttonStart2, endButton2);

        Scene scene = new Scene(group, 550, 100);
        stage.setScene(scene);

        stage.setTitle("Task1 b");

        stage.show();
    }

    @Override
    public void stop()
    {
        TThread1.interrupt();
        TThread2.interrupt();
    }
}