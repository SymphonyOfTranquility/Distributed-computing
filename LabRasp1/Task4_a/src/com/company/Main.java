package com.company;

public class Main {

    public static void main(String[] args) {
	// write your code here
        ReadersAndWriters readersAndWriters = new ReadersAndWriters();
        readersAndWriters.findNameByPhone(3);
        readersAndWriters.findPhoneByName(3);
        readersAndWriters.addNewInfo(4);
        readersAndWriters.joinAll();
    }
}
