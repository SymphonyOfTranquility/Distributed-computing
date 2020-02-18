package com.company;

import java.io.*;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.locks.ReadWriteLock;

public class ReadersAndWriters {
    private String[] phones;
    private String[] names;
    private int size;

    private ReadWriteLock lock;
    private Thread[] readPhones;
    private Thread[] readNames;
    private Thread[] writers;
    private String fileName;
    private int numberOfIterations;
    private Random random;

    public ReadersAndWriters(){
        random = new Random();
        size = 10;
        numberOfIterations = 10;
        phones = new String[size];
        names = new String[size];
        for (int i = 0;i < size; ++i){
            phones[i] = "phone_" + i;
            names[i] = "name_" + i;
        }

        File file = new File("data.txt");
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            FileWriter writeToFile = new FileWriter("data.txt");
            writeToFile.write("phone_0 name_0");
            writeToFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void findNameByPhone(int numberOfThreads){
        readNames = new Thread[numberOfThreads];
        for (int i = 0;i < numberOfThreads; ++i)
        {
            readNames[i] = new Thread(()->{
                for (int j = 0;j < numberOfIterations; ++j)
                {
                    lock.readLock().lock();
                    String str = "";
                    try {
                        File file = new File("data.txt");
                        FileInputStream fis = new FileInputStream(file);
                        byte[] data = new byte[(int) file.length()];
                        fis.read(data);
                        fis.close();
                        str = new String(data, "UTF-8");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    lock.readLock().unlock();
                    Scanner scanner = new Scanner(str);

                    String phone = phones[random.nextInt(size)];
                    String name = "";

                    while (scanner.hasNextLine()){
                        String info = scanner.nextLine();
                        String[] strings = info.split(" ");
                        if (strings[0] == phone){
                            name = strings[1];
                            break;
                        }
                    }
                    if (name.equals("")){
                        System.out.println("Person with phone " + phone + " wasn't found.");
                    }
                    else {
                        System.out.println("Person with phone " + phone + " is " + name + ".");
                    }
                }
            });
        }
    }

}
