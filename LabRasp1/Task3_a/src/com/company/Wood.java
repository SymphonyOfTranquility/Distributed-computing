package com.company;

public class Wood {
    private Semaphore wakeUpWinnie;
    private Semaphore giveHoney;
    private int currentVolume;
    private int maxVolume;
    private int numberOfIterations;
    private int maxNumberOfIterations;
    private Thread bear;
    private Thread[] bees;
    private int numberOfBees;
    private volatile boolean runningBees;

    Wood(int maxNumberOfIterations, int maxVolume, int numberOfBees) {
        wakeUpWinnie = new Semaphore();
        giveHoney = new Semaphore();
        numberOfIterations = 0;
        this.maxNumberOfIterations = maxNumberOfIterations;
        this.numberOfBees = numberOfBees;
        this.maxVolume = maxVolume;
    }

    void eatHoney(){
        bear = new Thread(()->{
            while (!Thread.interrupted())
            {
                try {
                    wakeUpWinnie.lock();
                    giveHoney.lock();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                currentVolume = 0;
                System.out.println("Cup is empty");

                ++numberOfIterations;

                if (numberOfIterations == maxNumberOfIterations)
                {
                    bear.interrupt();
                    runningBees = false;
                }
                try {
                    giveHoney.unlock();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        bear.start();
    }

    public void collectHoney()
    {
        runningBees = true;

        bees = new Thread[numberOfBees];
        for (int i = 0;i < numberOfBees; ++i)
        {
            int finalI = i;
            bees[i] = new Thread(() -> {
                while (runningBees)
                {
                    try {
                        giveHoney.lock();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("Bee #" + finalI + " bring some honey");
                    if (currentVolume+1 < maxVolume) {
                        ++currentVolume;
                    }
                    else{
                        try {
                            wakeUpWinnie.unlock();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    try {
                        giveHoney.unlock();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            bees[i].start();
        }
    }
}
