package com.company;

public class Worker implements Runnable {
    private int id;
    private Main matr;
    public Worker(int id, Main matr){
        this.id = id;
        this.matr = matr;
    }

    @Override
    public void run() {
        matr.tapeCircuit(id);
    }
}
