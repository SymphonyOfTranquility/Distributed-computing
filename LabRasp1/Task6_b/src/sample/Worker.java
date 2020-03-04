package sample;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.locks.ReentrantLock;

public class Worker implements Runnable {

    private int size;
    private int[][] exists;
    private CyclicBarrier barrier, barrierForShow;
    private Worker[] others;
    private int workerId;
    private ReentrantLock locker;

    Worker(int size, int[][] exists, CyclicBarrier barrier, CyclicBarrier barrierForShow, int workerId, ReentrantLock locker){
        this.workerId = workerId;
        this.exists = new int[size][size];
        for (int i = 0;i < size; ++i)
            for (int j = 0;j < size; ++j)
                if (exists[i][j] != workerId)
                    this.exists[i][j] = 0;
                else
                    this.exists[i][j] = workerId;
        this.size = size;
        this.barrier = barrier;
        this.barrierForShow = barrierForShow;
        this.locker = locker;
    }

    void setOthers(Worker[] others){
        this.others = others;
    }

    int getWorkerId() {
        return workerId;
    }

    private int countArea(int i, int j) {
        int counter = 0;
        for (int u = -1;u < 2; ++u)
            for (int v = -1;v < 2; ++v)
                if (i+u >= 0 && i+u < size && j+v >= 0 && j+v < size && exists[i+u][j+v] == workerId)
                    ++counter;
        if (exists[i][j] == workerId)
            --counter;
        return counter;
    }

    int getValue(int i, int j){
        if (i < 0 || j < 0 || i >= size || j >= size)
            return 0;
        return exists[i][j];
    }

    private int checkAll(int i, int j) {
        int counter = countArea(i, j);

        if (exists[i][j] == 0 && counter == 3)
            return workerId;
        else if (exists[i][j] == 0)
            return 0;

        return (counter == 2 || counter == 3) ? workerId : 0;
    }

    private boolean isFree(int i, int j) {
        for (int k = 0;k < others.length; ++k) {
            int otherId = others[k].getWorkerId();
            if (otherId != workerId && others[k].getValue(i, j) != 0) {
                return false;
            }
        }
        return true;
    }

    public int[][] getExists(){
        return exists;
    }

    @Override
    public void run() {
        while (!Thread.interrupted()){
            int[][] tempAnswer = new int[size][size];
            for (int i = 0;i < size; ++i)
            {
                for (int j = 0;j < size; ++j)
                {
                    if (isFree(i, j)) {
                        locker.lock();
                        tempAnswer[i][j] = checkAll(i, j);
                        locker.unlock();
                    }
                }
            }
            try {
                barrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                Thread.currentThread().interrupt();
            }
            exists = tempAnswer;
            try {
                barrierForShow.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
