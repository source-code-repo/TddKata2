package com;

import java.util.Scanner;

public class MainClass {
    public static final String RESULT_OUTPUT = "The result is ";
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        StringCalculator sc = new StringCalculator();
        int result = sc.add(args[0]);
        System.out.println(RESULT_OUTPUT + result);
        System.out.println("another input please: ");
        String s = scanner.nextLine();
        result = sc.add(s);
        System.out.println(RESULT_OUTPUT + result);
    }

    public static void setScanner(Scanner s) {
        scanner = s;
    }
}
