package com.company;

import java.util.Random;

public class Main {

    private int SIZE = 5000;
    private int numberOfProcess = 2;
    private float[] mat1 = new float[SIZE*SIZE];
    private float[] mat2 = new float[SIZE*SIZE];
    private float[] mat_ans = new float[SIZE*SIZE];

    private Random random = new Random();

    void set_rand() {
        for (int i = 0;i < SIZE*SIZE; ++i)
            mat1[i] = (random.nextFloat()-0.5f)*2.f;
        for (int i = 0;i < SIZE*SIZE; ++i)
            mat2[i] = (random.nextFloat()-0.5f)*2.f;
    }

    void tapeCircuit(int pid) {
        int start = pid*(SIZE/numberOfProcess);
        int end = (pid+1)*(SIZE/numberOfProcess);
        for (int i = start;i <  end; ++i) {
            for (int j = 0;j < SIZE; ++j) {
                mat_ans[i*SIZE + j] = 0.f;
                for (int k = 0;k < SIZE; ++k) {
                    mat_ans[i*SIZE + j] += mat1[i*SIZE + k] + mat2[k*SIZE + j];
                }
            }
        }
    }

    public static void main(String[] args) {
        Main matr = new Main();
        matr.set_rand();

        Thread[] threads = new Thread[matr.numberOfProcess];
        Worker[] workers = new Worker[matr.numberOfProcess];
        long start = System.nanoTime();
        for (int i = 0;i < matr.numberOfProcess; ++i) {
            workers[i] = new Worker(i, matr);
            threads[i] = new Thread(workers[i]);
            threads[i].start();
        }
        for (int i = 0;i < matr.numberOfProcess; ++i) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        long elapsedTime = System.nanoTime() - start;
        System.out.println(elapsedTime/1000000000.0);
    }
}
