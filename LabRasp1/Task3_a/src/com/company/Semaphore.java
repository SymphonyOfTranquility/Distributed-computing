package com.company;

import java.util.concurrent.locks.ReentrantLock;

public class Semaphore {
    private boolean isLocked;

    public Semaphore()
    {
        isLocked = false;
    }

    public synchronized void lock() throws InterruptedException {
        if (isLocked)
            this.wait();
        isLocked = true;
    }

    public synchronized void unlock() throws InterruptedException {
        isLocked = false;
        this.notify();
    }
}
