package com.company;

import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.locks.ReentrantLock;

public class Recruits {
    private Thread[] threads;
    private int numberOfThreads;
    private int numberOfRecruits;
    private boolean[] recruits;
    private CyclicBarrier barrier;
    private Random random;
    private ReentrantLock locker;
    private boolean[] isRotated;

    private class Worker implements Runnable {

        private int index;

        public Worker(int index)
        {
            this.index = index;
        }
        @Override
        public void run()
        {
            while (!Thread.interrupted())
            {
                locker.lock();
                isRotated[index] = false;
                locker.unlock();
                for (int i = index;i < numberOfRecruits; i += numberOfThreads)
                {
                    locker.lock();
                    if (recruits[i] && i < numberOfRecruits -1 && !recruits[i+1]){
                        recruits[i] = !recruits[i];
                        recruits[i+1] = !recruits[i+1];
                        isRotated[index] = true;
                    }
                    else if (!recruits[i] && i > 0 && recruits[i-1]){
                        recruits[i] = !recruits[i];
                        recruits[i-1] = !recruits[i-1];
                        isRotated[index] = true;
                    }
                    locker.unlock();
                }
                try {
                    barrier.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    Recruits(int numberOfRecruits)
    {
        this.numberOfRecruits = numberOfRecruits;
        numberOfThreads = numberOfRecruits/50;
        threads = new Thread[numberOfThreads];
        isRotated = new boolean[numberOfThreads];
        recruits = new boolean[numberOfRecruits];
        barrier = new CyclicBarrier(numberOfThreads, ()->{
            boolean ok = false;
            for (int i = 0;i < numberOfThreads; ++i)
                ok = ok|isRotated[i];
            if (!ok)
            {
                System.out.println("Done");
                for (int i = 0;i < numberOfThreads; ++i)
                    threads[i].interrupt();
            }
        });
        random = new Random();
        locker = new ReentrantLock();
        for (int i = 0;i < numberOfRecruits; ++i)
        {
            if (random.nextInt(2) == 1)
                recruits[i] = true;
            else
                recruits[i] = false;
        }
    }

    void orderRecruits()
    {
        for (int i = 0;i < numberOfThreads; ++i)
        {
            threads[i] = new Thread(new Worker(i));
            threads[i].start();
        }
    }

    void endRecruits()
    {
        for (int i = 0;i < numberOfThreads; ++i)
        {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                threads[i].interrupt();
            }
        }
    }
}
