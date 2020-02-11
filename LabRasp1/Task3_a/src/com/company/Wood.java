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
                    wakeUpWinnie.release();
                    giveHoney.release();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                currentVolume = 0;
                System.out.println("Cup #" + (numberOfIterations + 1) + " is empty");

                ++numberOfIterations;

                if (numberOfIterations == maxNumberOfIterations)
                {
                    bear.interrupt();
                    runningBees = false;
                }
                giveHoney.take();
            }
        });
        bear.start();
    }

    public void collectHoney()
    {
        runningBees = true;

        giveHoney.take();
        bees = new Thread[numberOfBees];
        for (int i = 0;i < numberOfBees; ++i)
        {
            bees[i] = new Thread(() -> {
                while (runningBees)
                {
                    try {
                        giveHoney.release();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (currentVolume < maxVolume){
                        ++currentVolume;
                        if (currentVolume == maxVolume) {
                            System.out.println("\nWake up Winnie!");
                            wakeUpWinnie.take();
                        }
                    }

                    giveHoney.take();
                }
            });
            bees[i].start();
        }
    }

    public void endWork(){
        try {
            bear.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
