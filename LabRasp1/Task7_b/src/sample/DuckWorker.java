package sample;

import javafx.application.Platform;
import javafx.scene.image.ImageView;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;

public class DuckWorker implements Runnable {

    private double direction;      // -1 - left , 1 - right
    private double speed;
    private double currentPos;
    private ReentrantLock locker;
    private DuckImgView duck;
    private boolean canMove;
    private ReadWriteLock overallStopMove;

    private ReentrantLock localLock;

    DuckWorker(ReentrantLock locker, int direction, int speed, int currentPos, DuckImgView duck, ReadWriteLock overallStopMove) {
        this.locker = locker;
        this.direction = direction;
        this.speed = speed;
        this.duck = duck;
        this.currentPos = currentPos;
        this.overallStopMove = overallStopMove;
        canMove = false;
        localLock = new ReentrantLock();
    }

    void setMove(boolean newVal) {
        canMove = newVal;
    }

    @Override
    public void run() {
        overallStopMove.readLock().lock();
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

        if (canMove) {
            currentPos += direction * speed;
            if (currentPos > 1000 || currentPos < -200)
                canMove = false;
            synchronized (duck) {
                duck.setX(currentPos);
            }
        }
        overallStopMove.readLock().unlock();
    }
}
