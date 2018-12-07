package com.phoenix;

import java.util.Calendar;
import java.util.Scanner;

public class Main {

    static Scanner keyboard = new Scanner(System.in);

    private static void printOperations() {
        System.out.println("1. Add event");
        System.out.println("2. Add task");
        System.out.println("3. Add long event");
        System.out.println("5. Show upcoming events");
        System.out.println("6. Show events in a date range");
        System.out.println("0. Exit");
    }

    private static void choiceCollector(int choice) {
        switch (choice) {
            case 1:
                DatabaseOperator.addEvent();
                break;

            case 2:
                DatabaseOperator.addTask();
                break;

            case 3:
                DatabaseOperator.addDurableEvent();
                break;

            case 5:
                DatabaseOperator.allFutureEvents().forEach(System.out::println);
                break;

            case 6:
                System.out.print("From ");
                Calendar begin = Wizards.timeWizard();
                System.out.print("To ");
                Calendar end = Wizards.timeWizard();
                DatabaseOperator.eventsBetween(begin, end).forEach(System.out::println);
                break;
            case 0:
                System.out.println("Exiting ");
                break;
        }
    }


    public static void main(String[] args) {

        Thread conn = new Thread(DatabaseOperator::establishConnection);

        // MAIN THREAD.
        Thread main = new Thread(() -> {
            try {
                conn.start();
                conn.join();

            } catch (InterruptedException e) {
            }
            String code;
            do {
                printOperations();
                code = keyboard.nextLine();
                choiceCollector(Integer.valueOf(code));
            } while (Integer.valueOf(code) != 0);
        });

        main.start();
    }
}


