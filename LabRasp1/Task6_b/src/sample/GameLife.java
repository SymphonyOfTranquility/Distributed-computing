package sample;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.locks.ReentrantLock;

public class GameLife {
    private int[][] exists;
    private int size;
    private Thread[] threadsCivil;
    private CyclicBarrier barrier, barrierForShow;
    private ReentrantLock locker;

    public GameLife(int size){
        this.size = size;
        exists = new int[size][size];
        for (int i = 0;i < size; ++i)
            for (int j = 0;j < size; ++j)
                exists[i][j] = 0;
    }

    public int getValue(int i, int j){
        if (i >= size || j >= size || i < 0 || j < 0)
            return 0;
        return exists[i][j];
    }

    public void start(MyRectangles[][] rectangles){
        for (int i = 0;i < size; ++i)
            for (int j = 0;j < size; ++j)
                exists[i][j] = rectangles[i][j].getColor();

        threadsCivil = new Thread[4];
        Worker[] workers = new Worker[4];
        locker = new ReentrantLock();
        barrier = new CyclicBarrier(4);
        barrierForShow = new CyclicBarrier(4, ()-> {
            int[][][] tempExists = new int[4][][];
            for (int i = 0;i < 4; ++i)
                tempExists[i] = workers[i].getExists();

            for (int i = 0;i < size; ++i) {
                for (int j = 0;j < size; ++j){
                    int last = 0;
                    for (int k = 0;k < 4; ++k)
                        if (tempExists[k][i][j] != 0)
                            last = tempExists[k][i][j];
                    exists[i][j] = last;
                }
            }

            for (int i = 0;i < size; ++i) {
                for (int j = 0;j < size; ++j)
                    rectangles[i][j].setColor(exists[i][j]);
            }
            try {
                Thread.sleep(160);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        for (int i = 0;i < 4; ++i)
            workers[i] = new Worker(size, exists, barrier, barrierForShow, i+1, locker);

        for (int i = 0;i < 4; ++i)
            workers[i].setOthers(workers);

        for (int i = 0;i < 4; ++i){
            threadsCivil[i] = new Thread(workers[i]);
            threadsCivil[i].start();
        }
    }

    public void stop(){
        for (int i = 0;i < 4; ++i)
            threadsCivil[i].interrupt();
    }
}
