package com.phoenix;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner;
import java.util.stream.Stream;

public class Main {

    static Scanner keyboard = new Scanner(System.in);

    private static ArrayList<iEvent> cache = new ArrayList<>(10);

    private static void printOperations() {
        System.out.println("1. Add event");
        System.out.println("2. Add task");
        System.out.println("3. Add long event");
        System.out.println("4. Add conference");
        System.out.println("5. Show upcoming events");
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
                DatabaseOperator.upcoming();
                break;

            case 0:
                System.out.println("Exiting ");
                break;
        }
    }

    public static void main(String[] args) {
        String code;
        do {
            printOperations();
            code = keyboard.next();
            choiceCollector(Integer.valueOf(code));
            cache.sort(Comparator.comparing(iEvent::getCalendar));
        } while (Integer.valueOf(code) != 0);

        Stream.of(cache).forEachOrdered(System.out::println);
    }
}
