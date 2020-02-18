package com.company;

import java.io.*;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReadersAndWriters {
    private String[] phones;
    private String[] names;
    private boolean[] used;
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
        size = 25;
        numberOfIterations = 4;
        phones = new String[size];
        names = new String[size];
        used = new boolean[size];
        lock = new ReentrantReadWriteLock();
        fileName = "data.txt";
        for (int i = 0;i < size; ++i){
            phones[i] = "phone_" + i;
            names[i] = "name_" + i;
            used[i] = false;
        }

        File file = new File(fileName);
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            FileWriter writeToFile = new FileWriter(fileName);
            writeToFile.write("phone_0 name_0");
            used[0] = true;
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
                        File file = new File(fileName);
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
                        if (strings[0].equals(phone)){
                            name = strings[1];
                            break;
                        }
                    }
                    if (name.equals("")){
                        System.out.println("Person with " + phone + " wasn't found.");
                    }
                    else {
                        System.out.println("Person with " + phone + " is " + name + ".");
                    }
                    scanner.close();
                }
            });
            readNames[i].start();
        }
    }

    public void findPhoneByName(int numberOfThreads){
        readPhones = new Thread[numberOfThreads];
        for (int i = 0;i < numberOfThreads; ++i)
        {
            readPhones[i] = new Thread(()->{
                for (int j = 0;j < numberOfIterations; ++j)
                {
                    lock.readLock().lock();
                    String str = "";
                    try {
                        File file = new File(fileName);
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

                    String name = names[random.nextInt(size)];
                    String phone = "";

                    while (scanner.hasNextLine()){
                        String info = scanner.nextLine();
                        String[] strings = info.split(" ");
                        if (strings[1].equals(name)){
                            phone = strings[1];
                            break;
                        }
                    }
                    if (phone.equals("")){
                        System.out.println("Phone for person " + name + " wasn't found.");
                    }
                    else {
                        System.out.println("Phone for person " + name + " is " + phone + ".");
                    }
                    scanner.close();
                }
            });
            readPhones[i].start();
        }
    }

    public void addNewInfo(int numberOfThreads){
        writers = new Thread[numberOfThreads];
        for (int i = 0;i < numberOfThreads; ++i){
            writers[i] = new Thread(()->{
                for (int j = 0;j < numberOfIterations; ++j){
                    lock.writeLock().lock();
                    int add = -1;
                    for (int k = 0;k < size; ++k)
                        if (!used[k])
                        {
                            add = k;
                            break;
                        }
                    if (add != -1 && random.nextInt()%4 != 2) {
                        used[add] = true;
                        String name = names[add];
                        String phone = phones[add];
                        System.out.println("Added person: " + name + " phone is: " + phone + ".");
                        try {
                            FileWriter fileWriter = new FileWriter(fileName);
                            BufferedWriter br = new BufferedWriter(fileWriter);
                            PrintWriter pr = new PrintWriter(br);
                            pr.println(phone + " " + name);
                            pr.close();
                            br.close();
                            fileWriter.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    else {
                        RandomAccessFile f = null;
                        try {
                            f = new RandomAccessFile(fileName, "rw");
                            long length = f.length() - 1;

                            if (length == 0){
                                System.out.println("List is empty");
                                continue;
                            }
                            byte b = 1;
                            String del = "";
                            do {
                                length -= 1;
                                f.seek(length);
                                b = f.readByte();
                                del += (char) b;
                            } while(b != 10 && length > 0);
                            if (length != 0)
                                f.setLength(length+1);
                            f.close();
                            StringBuilder sb = new StringBuilder(del);
                            del = sb.reverse().toString();
                            String[] strings = del.split(" ");
                            System.out.println("Deleted person " + strings[1] + " with " + strings[0]);
                            for (int k = 0;k < size; ++k)
                                if (names[k].equals(strings[1])) {
                                    used[k] = false;
                                    break;
                                }
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    lock.writeLock().unlock();
                }
            });
            writers[i].start();
        }
    }

    public void joinAll(){
        for (int i = 0;i < writers.length; ++i) {
            try {
                writers[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        for (int i = 0;i < readNames.length; ++i) {
            try {
                readNames[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        for (int i = 0;i < readPhones.length; ++i) {
            try {
                readPhones[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
