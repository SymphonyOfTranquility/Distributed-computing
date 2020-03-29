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
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;

import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.io.FileInputStream;
import java.lang.management.ClassLoadingMXBean;
import java.sql.Struct;
import java.util.Random;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Main extends Application {

    DuckImgView[] imgDucks;
    DuckWorker[] ducks;
    Thread[] duckThreads;
    int maxNumber = 10;
    Boolean[] canMove;
    ReentrantLock locker = new ReentrantLock();
    ReadWriteLock overAllStop = new ReentrantReadWriteLock();
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
            ducks[i] = new DuckWorker(locker,-1, -1, 0, newDuck, overAllStop);
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
        stage.setMaxHeight(600);
        stage.setMaxWidth(800);
        scene.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                ImageView bulletImg = new ImageView(new Image("sample/img/bullet.png"));
                bulletImg.setX(400);
                bulletImg.setY(600);
                bulletImg.setRotate(Math.atan2(-400+mouseEvent.getSceneX(), 600-mouseEvent.getSceneY())*180.0/Math.PI);
                double sqrt = Math.sqrt((400-mouseEvent.getSceneX())*(400-mouseEvent.getSceneX()) +
                        (600-mouseEvent.getSceneY())*(600-mouseEvent.getSceneY()))/3.5;
                double bulletSpeedX = (400-mouseEvent.getSceneX())/sqrt;
                double bulletSpeedY = (600-mouseEvent.getSceneY())/sqrt;
                final boolean[] working = {true};
                Runnable hunt = new Runnable() {
                    @Override
                    public void run() {
                        overAllStop.writeLock().lock();
                        for (int i = 0; i < maxNumber; ++i) {
                            if (imgDucks[i].getLayoutBounds().intersects(bulletImg.getLayoutBounds())) {
                                synchronized (ducks) {
                                    ducks[i].setMove(false);
                                }
                                working[0] = false;
                                synchronized (bulletImg) {
                                    synchronized (group) {
                                        group.getChildren().remove(bulletImg);
                                    }
                                }
                                break;
                            }
                        }
                        synchronized (bulletImg) {
                            bulletImg.setX(bulletImg.getX() - bulletSpeedX);
                            bulletImg.setY(bulletImg.getY() - bulletSpeedY);
                            if (bulletImg.getX() > 800 || bulletImg.getX() < 0 || bulletImg.getY() < 0) {
                                working[0] = false;
                                synchronized (group) {
                                    group.getChildren().remove(bulletImg);
                                }
                            }
                        }
                        overAllStop.writeLock().unlock();

                    }
                };
                group.getChildren().addAll(bulletImg);
                Thread forHunt = new Thread(()->
                {
                    while (!Thread.interrupted() && working[0])
                    {
                        try {
                            Thread.sleep(7, 10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            Thread.currentThread().interrupt();
                        }
                        Platform.runLater(hunt);
                    }
                });
                forHunt.setDaemon(true);
                forHunt.start();

            }
        });


        stage.setTitle("Task7 b");
        stage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}

