package sample;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.stage.Stage;

public class Main extends Application {

    private final int SIZE = 40;
    private Rectangle[][] rectangles;

    @Override
    public void start(Stage stage) throws Exception{

        rectangles = new Rectangle[SIZE][SIZE];
        GridPane gridPane = new GridPane();
        for (int i = 0;i < SIZE; ++i)
        {
            for (int j = 0;j < SIZE; ++j) {
                rectangles[i][j] = new Rectangle(15, 15);
                rectangles[i][j].setStrokeType(StrokeType.INSIDE);
                rectangles[i][j].setStroke(Color.BLACK);
                rectangles[i][j].setFill(Color.web("0x2F4F4F"));
                rectangles[i][j].setOnMouseClicked(new EventHanglerForRectangles(rectangles[i][j]));
                gridPane.add(rectangles[i][j], i, j);
            }
        }
        gridPane.setLayoutX(50);
        gridPane.setLayoutY(50);

        Button startButton = new Button("Start");
        startButton.setLayoutX(680);
        startButton.setLayoutY(50);
        startButton.setPrefSize(50, 20);
        Button stepButton = new Button("Step");
        stepButton.setLayoutX(680);
        stepButton.setLayoutY(80);
        stepButton.setPrefSize(50, 20);
        Button stopButton = new Button("Stop");
        stopButton.setLayoutX(680);
        stopButton.setLayoutY(110);
        stopButton.setPrefSize(50, 20);

        Group group = new Group();
        group.getChildren().add(gridPane);
        group.getChildren().add(startButton);
        group.getChildren().add(stepButton);
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
