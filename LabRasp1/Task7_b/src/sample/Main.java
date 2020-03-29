package sample;


import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.io.FileInputStream;
import java.lang.management.ClassLoadingMXBean;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

public class Main extends Application {

    DuckImgView[] imgDucks;
    DuckWorker[] ducks;
    Thread[] duckThreads;
    int maxNumber = 10;
    Boolean[] canMove;
    ReentrantLock locker = new ReentrantLock();
    Random random = new Random();

    @Override
    public void start(Stage stage) throws Exception{

        // create a image
        Image imageBack = new Image("sample/img/gameBackground.png");
        ImageView backgroundImageView = new ImageView(imageBack);

        imgDucks = new DuckImgView[maxNumber];
        canMove = new Boolean[maxNumber];
        ducks = new DuckWorker[maxNumber];
        duckThreads = new Thread[maxNumber];

        for (int i = 0;i < maxNumber; ++i) {
            DuckImgView newDuck = new DuckImgView(i);
            imgDucks[i] = newDuck;
            ducks[i] = new DuckWorker(locker,-1, -1, 0, newDuck);
            newDuck.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    locker.lock();
                    ducks[newDuck.id].setMove(false);
                    locker.unlock();
                }
            });
            duckThreads[i] = new Thread(()->
            {
                while (!Thread.interrupted()) {
                    try {
                        Thread.sleep(10, 100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        Thread.currentThread().interrupt();
                    }
                    Platform.runLater(ducks[newDuck.id]);
                }
            });
            duckThreads[i].setDaemon(true);
            duckThreads[i].start();
        }

        Group group = new Group();
        group.getChildren().addAll(backgroundImageView);
        for (int i = 0;i < maxNumber; ++i)
            group.getChildren().addAll(imgDucks[i]);
        Scene scene = new Scene(group, 800, 600);
        stage.setScene(scene);

        stage.setTitle("Task7 a");
        stage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}

