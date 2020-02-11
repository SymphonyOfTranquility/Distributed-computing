package com.company;

import java.util.Random;

public class Wood {
    private final boolean[][] winniePosition;
    private boolean[] winnieIsHere;
    private int width;
    private int height;

    private int xWinnie;
    private int yWinnie;
    private Thread winnie;
    private Random random;

    Wood(int width, int height)
    {
        this.width = width;
        this.height = height;
        xWinnie = 0;
        yWinnie = 0;
        random = new Random();
        winnieIsHere = new boolean[height];
        winniePosition = new boolean[width][height];
        for (int i = 0;i < width; ++i)
            for (int j = 0;j < height; ++j)
                winniePosition[i][j] = false;

        winniePosition[0][0] = true;
    }

    public void winnieMoving()
    {
        winnie = new Thread(()->{
           while (!Thread.interrupted())
           {
               int direction = random.nextInt(4);
               int xOffSet = 0, yOffSet = 0;
               if (direction == 0 && xWinnie > 0 || direction == 1 && xWinnie == width-1)
                   xOffSet = -1;
               if (direction == 1 && xWinnie < width-1 || direction == 0 && xWinnie == 0)
                   xOffSet = 1;

               if (direction == 2 && yWinnie > 0 || direction == 3 && yWinnie == height-1)
                   yOffSet = -1;
               if (direction == 3 && yWinnie < height-1 || direction == 2 && yWinnie == 0)
                   yOffSet = 1;

               synchronized (winniePosition)
               {
                   winniePosition[xWinnie][yWinnie] = false;
                   xWinnie += xOffSet;
                   yWinnie += yOffSet;
                   winniePosition[xWinnie][yWinnie] = true;
               }
           }
        });

        winnie.start();
    }

    public void stopWinnie(){
        winnie.interrupt();
    }

    private void findWinnieInRow(int index)
    {
        boolean find = false;
        synchronized (winniePosition)
        {
            for (int i = 0;i < width; ++i)
                if (winniePosition[index][i]){
                    find = true;
                    break;
                }
        }
        winnieIsHere[index] = find;
    }

    public boolean findWinnie()
    {
        for (int i = 0;i < height; ++i)
            winnieIsHere[i] = false;

        Thread[] bees = new Thread[height];
        for (int i = 0;i < height; ++i)
        {
            int finalI = i;
            bees[i] = new Thread(()->{
                findWinnieInRow(finalI);
            });
            bees[i].start();
        }
        for (int i = 0;i < height; ++i) {
            try {
                bees[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        for (int i = 0;i < height; ++i)
            if (winnieIsHere[i])
                return true;

        return false;
    }


}
