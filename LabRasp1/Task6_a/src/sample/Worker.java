package sample;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.locks.ReentrantLock;

public class Worker implements Runnable {

    private int  size;
    private boolean[][] exists;
    private CyclicBarrier barrier, barrierForShow;
    private Worker nearWorkerX, nearWorkerY, workerDiag;
    private int workerId;

    Worker(int startX, int startY, int size, boolean[][] exists, CyclicBarrier barrier, CyclicBarrier barrierForShow, int workerId){
        this.size = size;
        this.exists = new boolean[size][size];
        for (int i = 0;i < size; ++i)
            for (int j = 0; j < size; ++j)
                this.exists[i][j] = exists[i+startX][j+startY];
        this.barrier = barrier;
        this.workerId = workerId;
        this.barrierForShow = barrierForShow;
    }

    int getWorkerId() {
        return workerId;
    }

    void setWorkerX(Worker workerX){
        nearWorkerX = workerX;
    }
    void setWorkerY(Worker workerY){
        nearWorkerY = workerY;
    }
    void setWorkerDiag(Worker workerDiag){
        this.workerDiag = workerDiag;
    }

    private int countArea(int i, int j) {
        int counter = 0;
        for (int u = -1;u < 2; ++u)
            for (int v = -1;v < 2; ++v)
                if (i+u >= 0 && i+u < size && j+v >= 0 && j+v < size && exists[i+u][j+v])
                    ++counter;
        if (exists[i][j])
            --counter;
        return counter;
    }

    boolean getValue(int i, int j){
        if (i < 0 || j < 0 || i >= size || j >= size)
            return false;
        return exists[i][j];
    }

    private boolean checkAll(int i, int j, int flag) {
        int counter = countArea(i, j);
        for (int u = -1;u < 2; ++u) {
            if (nearWorkerX.getValue(i + u, size - 1 - j) && (flag == 1 || flag == 3))
                ++counter;
            if (nearWorkerY.getValue(size - 1 - i, j + u) && (flag == 2 || flag == 3))
                ++counter;
        }
        if (workerDiag.getValue(size - 1 - i, size - 1 - j) && flag == 3)
            ++counter;

        if (!exists[i][j] && counter == 3)
            return true;
        else if (!exists[i][j])
            return false;

        return counter == 2 || counter == 3;
    }

    public boolean[][] getExists(){
        return exists;
    }

    @Override
    public void run() {
        while (!Thread.interrupted()){
            boolean[][] tempAnswer = new boolean[size][size];
            for (int i = 0;i < size; ++i)
            {
                for (int j = 0;j < size; ++j)
                {
                    if (i <= 0 || j <= 0 || i >= size - 1 || j >= size - 1) {
                        int workerDiagId = workerDiag.getWorkerId();
                        if ((j == 0 && i == 0 && workerDiagId == 1) ||
                                (i == 0 && j == size - 1 && workerDiagId == 2) ||
                                (i == size - 1 && j == 0 && workerDiagId == 3) ||
                                (i == size - 1 && j == size - 1 && workerDiagId == 4)) {

                            tempAnswer[i][j] = checkAll(i, j, 3);
                            continue;
                        }

                        int workerIdY = nearWorkerY.getWorkerId();
                        if (i == 0 && (workerIdY == 1 || workerIdY == 2) ||
                                i == size - 1 && (workerIdY == 3 || workerIdY == 4)) {

                            tempAnswer[i][j] = checkAll(i, j, 2);
                            continue;
                        }
                        int workerIdX = nearWorkerX.getWorkerId();
                        if (j == 0 && (workerIdX == 1 || workerIdX == 3) ||
                                (j == size - 1 && (workerIdX == 2 || workerIdX == 4))) {

                            tempAnswer[i][j] = checkAll(i, j, 1);
                            continue;
                        }
                    }
                    tempAnswer[i][j] = checkAll(i, j, 0);
                }
            }
            try {
                barrier.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
            exists = tempAnswer;
            try {
                barrierForShow.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
        }
    }
}
