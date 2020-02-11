package com.company;

import java.util.concurrent.Semaphore;

public class Barbershop {
    private Semaphore hairdresserWait;
    private Semaphore hairdresserWork;
    private Semaphore visitorWait;

    private Thread hairdresser;

    public Barbershop (){
        hairdresserWait = new Semaphore(1, true);
        hairdresserWork = new Semaphore(1, true);
        visitorWait = new Semaphore(1, true);

        try {
            hairdresserWait.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        hairdresser = new Thread(()->{
            while (!Thread.interrupted()){
                try {
                    hairdresserWait.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("Visitor with new haircut");
                visitorWait.release();
                hairdresserWork.release();
            }
        });
        hairdresser.start();
    }

    public void addVisitor(){
        Thread visitor = new Thread(()->{
            try {
                hairdresserWork.acquire();
                hairdresserWait.release();
                visitorWait.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        visitor.start();
    }
}
