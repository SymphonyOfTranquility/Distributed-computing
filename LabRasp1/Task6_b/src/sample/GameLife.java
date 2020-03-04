package sample;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.locks.ReentrantLock;

public class GameLife {
    private boolean[][] exists;
    private int size;
    private Thread[] threads;
    private CyclicBarrier barrier, barrierForShow;
    private ReentrantLock locker;

    public GameLife(int size){
        this.size = size;
        exists = new boolean[size][size];
        for (int i = 0;i < size; ++i)
            for (int j = 0;j < size; ++j)
                exists[i][j] = false;
    }

    public boolean getValue(int i, int j){
        if (i >= size || j >= size || i < 0 || j < 0)
            return false;
        return exists[i][j];
    }

    public void start(MyRectangles[][] rectangles){
        for (int i = 0;i < size; ++i)
            for (int j = 0;j < size; ++j)
                exists[i][j] = rectangles[i][j].getChanged();

        threads = new Thread[4];
        Worker[] workers = new Worker[4];
        locker = new ReentrantLock();
        barrier = new CyclicBarrier(4);
        barrierForShow = new CyclicBarrier(4, ()-> {
            boolean[][][] tempExists = new boolean[4][][];
            for (int i = 0;i < 4; ++i)
                tempExists[i] = workers[i].getExists();

            for (int i = 0;i < size; ++i) {
                for (int j = 0;j < size; ++j){
                    if (i < size/2 && j < size/2){
                        exists[i][j] = tempExists[0][i][j];
                    } else if (i < size/2 && j >= size/2){
                        exists[i][j] = tempExists[1][i][j-size/2];
                    } else if (i >= size/2 && j < size/2){
                        exists[i][j] = tempExists[2][i-size/2][j];
                    } else if (i >= size/2 && j >= size/2){
                        exists[i][j] = tempExists[3][i-size/2][j-size/2];
                    }
                }
            }

            for (int i = 0;i < size; ++i) {
                for (int j = 0;j < size; ++j)
                    rectangles[i][j].setChanged(exists[i][j]);
            }
            try {
                Thread.sleep(160);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        workers[0] = new Worker(0, 0, size/2, exists, barrier, barrierForShow, 1);
        workers[1] = new Worker(0, size/2, size/2, exists, barrier, barrierForShow, 2);
        workers[2] = new Worker(size/2, 0, size/2, exists, barrier, barrierForShow, 3);
        workers[3] = new Worker(size/2, size/2, size/2, exists, barrier, barrierForShow, 4);
        workers[0].setWorkerX(workers[1]); workers[0].setWorkerY(workers[2]); workers[0].setWorkerDiag(workers[3]);
        workers[1].setWorkerX(workers[0]); workers[1].setWorkerY(workers[3]); workers[1].setWorkerDiag(workers[2]);
        workers[2].setWorkerX(workers[3]); workers[2].setWorkerY(workers[0]); workers[2].setWorkerDiag(workers[1]);
        workers[3].setWorkerX(workers[2]); workers[3].setWorkerY(workers[1]); workers[3].setWorkerDiag(workers[0]);

        for (int i = 0;i < 4; ++i){
            threads[i] = new Thread(workers[i]);
            threads[i].start();
        }
    }

    public void stop(){
        for (int i = 0;i < 4; ++i)
            threads[i].interrupt();
    }
}
