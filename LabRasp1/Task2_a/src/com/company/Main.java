package com.company;

public class Main {

    public static void main(String[] args) {
        Wood wood = new Wood(4, 4);

        wood.winnieMoving();
        for (int i = 0;i < 10; ++i)
        {
            System.out.println("Index i = " + i + ", answer = " + wood.findWinnie());
        }

        wood.stopWinnie();
    }
}
