package com.company;

import java.util.concurrent.Semaphore;

public class Barbershop {
    private Semaphore hairdresserWait;
    private Semaphore hairdresserWork;
    private Semaphore visitorWait;

    private Thread hairdresser;
    private Thread[] visitor;
    private volatile boolean runningHairdresser;

    public Barbershop (){
        hairdresserWait = new Semaphore(1, true);
        hairdresserWork = new Semaphore(1, true);
        visitorWait = new Semaphore(1, true);
    }

    public void start(){
        try {
            hairdresserWait.acquire();
            visitorWait.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        runningHairdresser = true;
        hairdresser = new Thread(()->{
            while (runningHairdresser){
                try {
                    hairdresserWait.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (!runningHairdresser)
                    return;
                System.out.println("Visitor with new haircut ");
                visitorWait.release();
                hairdresserWork.release();
            }
        });
        hairdresser.start();
    }

    public void addVisitors(int size){

        visitor = new Thread[size];
        for (int i = 0;i < size; ++i){
            visitor[i] = new Thread(()->{
                try {
                    hairdresserWork.acquire();
                    hairdresserWait.release();
                    visitorWait.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            visitor[i].start();
        }
        for (int i = 0;i < size; ++i) {
            try {
                visitor[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void close(){
        runningHairdresser = false;
        hairdresserWait.release();
        try {
            hairdresser.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
