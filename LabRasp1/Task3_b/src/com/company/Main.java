package com.company;

public class Main {

    public static void main(String[] args) {
	    Barbershop barbershop = new Barbershop();
	    barbershop.start();
	    barbershop.addVisitors(6);
		barbershop.close();
    }
}
