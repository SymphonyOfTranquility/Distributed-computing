package sample;

import javafx.application.Platform;
import javafx.scene.image.ImageView;

import java.util.concurrent.locks.ReentrantLock;

public class DuckWorker implements Runnable {

    private double direction;      // -1 - left , 1 - right
    private double speed;
    private double currentPos;
    private ReentrantLock locker;
    private DuckImgView duck;
    private boolean canMove;

    private ReentrantLock localLock;

    DuckWorker(ReentrantLock locker, int direction, int speed, int currentPos, DuckImgView duck) {
        this.locker = locker;
        this.direction = direction;
        this.speed = speed;
        this.duck = duck;
        this.currentPos = currentPos;
        canMove = false;
        localLock = new ReentrantLock();
    }

    void setMove(boolean newVal) {
        localLock.lock();
        canMove = newVal;
        localLock.unlock();
    }

    @Override
    public void run() {
        locker.lock();
        if (!canMove) {
            synchronized (duck){
                duck.setRandomStart();
                currentPos = duck.getX();
                speed = duck.random.nextDouble() * 1.3 + 0.1;
                if (duck.getX() > 400)
                    direction = -1;
                else
                    direction = 1;

                canMove = true;
            }
        }
        locker.unlock();

        localLock.lock();
        if (canMove) {
            currentPos += direction * speed;
            if (currentPos > 1000 || currentPos < -200)
                canMove = false;
            synchronized (duck) {
                duck.setX(currentPos);
            }
        }
        localLock.unlock();
    }
}
