package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.awt.*;
import java.io.FileInputStream;
import java.lang.management.ClassLoadingMXBean;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception{

        // create a image
        Image imageBack = new Image("sample/img/gameBackground.png");
        ImageView backgroundImageView = new ImageView(imageBack);

        Image imageDuck = new Image("sample/img/redLeft.gif");
        ImageView duck = new ImageView(imageDuck);
        duck.setX(50);
        duck.setY(50);
        Group group = new Group();
        group.getChildren().addAll(backgroundImageView);
        group.getChildren().addAll(duck);
        Scene scene = new Scene(group, 800, 600);
        stage.setScene(scene);

        stage.setTitle("Task7 a");
        stage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
