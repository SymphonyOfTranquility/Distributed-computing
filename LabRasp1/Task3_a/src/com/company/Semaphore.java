package com.company;

import java.util.concurrent.locks.ReentrantLock;

public class Semaphore {
    private int locker;

    public Semaphore(int locker)
    {
        this.locker = locker;
    }

    public synchronized void lock() throws InterruptedException {
        if (locker == 0)
            this.wait();
        --locker;
    }

    public synchronized void unlock() throws InterruptedException {
        ++locker;
        if (locker > 0)
            this.notify();
    }
}
