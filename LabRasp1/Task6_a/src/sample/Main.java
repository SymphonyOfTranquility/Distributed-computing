package sample;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Main extends Application {

    private final int SIZE = 40;
    private MyRectangles[][] rectEvents;
    private GameLife gameLife;

    @Override
    public void start(Stage stage) throws Exception{
        gameLife = new GameLife(SIZE);

        rectEvents = new MyRectangles[SIZE][SIZE];
        GridPane gridPane = new GridPane();
        for (int i = 0;i < SIZE; ++i)
        {
            for (int j = 0;j < SIZE; ++j) {
                rectEvents[i][j] = new MyRectangles(15);
                gridPane.add(rectEvents[i][j].getRectangle(), i, j);
            }
        }
        gridPane.setLayoutX(50);
        gridPane.setLayoutY(50);

        Button startButton = new Button("Start");
        Button stopButton = new Button("Stop");
        startButton.setLayoutX(680);
        startButton.setLayoutY(50);
        startButton.setPrefSize(60, 20);
        startButton.setOnAction(event -> {
            gameLife.start(rectEvents);
            startButton.setDisable(true);
            stopButton.setDisable(false);
        });
        stopButton.setOnAction(event -> {
            gameLife.stop();
            startButton.setDisable(false);
            stopButton.setDisable(true);
        });
        stopButton.setLayoutX(680);
        stopButton.setLayoutY(80);
        stopButton.setPrefSize(60, 20);

        Button angarButton = new Button("Angar");
        angarButton.setLayoutX(680);
        angarButton.setLayoutY(110);
        angarButton.setPrefSize(60, 20);
        angarButton.setOnAction(event -> {
            if (startButton.isDisabled())
                gameLife.stop();
            startButton.setDisable(false);
            stopButton.setDisable(true);
            for (int i = 0;i < SIZE; ++i)
                for (int j = 0;j < SIZE; ++j)
                    if (i%3 != 0 && j%3 != 0)
                        rectEvents[i][j].setChanged(true);
                    else
                        rectEvents[i][j].setChanged(false);
        });

        Button clearButton = new Button("Clear");
        clearButton.setLayoutX(680);
        clearButton.setLayoutY(140);
        clearButton.setPrefSize(60, 20);
        clearButton.setOnAction(event -> {
            if (startButton.isDisabled())
                gameLife.stop();
            startButton.setDisable(false);
            stopButton.setDisable(true);
            for (int i = 0;i < SIZE; ++i)
                for (int j = 0;j < SIZE; ++j)
                    rectEvents[i][j].setChanged(false);
        });

        Group group = new Group();
        group.getChildren().add(gridPane);
        group.getChildren().add(startButton);
        group.getChildren().add(angarButton);
        group.getChildren().add(clearButton);
        group.getChildren().add(stopButton);
        Scene scene = new Scene(group, 770, 700);
        scene.setFill(Color.GRAY);
        stage.setScene(scene);

        stage.setTitle("Task1 a");

        stage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
