package com.pluralsight;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.time.*;

import static java.lang.Double.*;


public class App  {
    static Scanner input = new Scanner(System.in);
    static ArrayList<Transaction> transactionsList = new ArrayList<>();



    public static void main(String[] args) {

        LocalDate currentDate = LocalDate.from(LocalDateTime.now());


        System.out.println(getCurrentLocalTime());


        extractFile("transactions.csv");


        runHomeScreen();


    }

    private static LocalTime getCurrentLocalTime() {
        LocalTime currentTime = LocalTime.from(LocalDateTime.now());


        DateTimeFormatter formattedTime = DateTimeFormatter.ofPattern("HH:mm:ss");
        String formattedTime1 = currentTime.format(formattedTime);
        LocalTime fullyFormattedTime = LocalTime.parse(formattedTime1);
        return fullyFormattedTime;
    }

    private static void extractFile(String fileName) {

        try {
            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);


            String input;
            bufferedReader.readLine();

            while ((input = bufferedReader.readLine()) != null) {
                String[] transactionAttributes = input.split("\\|");
                Transaction currentTransaction = new Transaction();
                currentTransaction.setDate(LocalDate.parse(transactionAttributes[0]));
                currentTransaction.setTime(LocalTime.parse(transactionAttributes[1]));
                currentTransaction.setDescription(transactionAttributes[2]);
                currentTransaction.setVendor(transactionAttributes[3]);
                currentTransaction.setAmount(parseDouble(transactionAttributes[4]));
                transactionsList.add(currentTransaction);
            }
            Collections.sort(transactionsList);

            bufferedReader.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private static void runHomeScreen() {
        boolean systemIsRunning = true;

        do {
            System.out.println("""
        ==================================================
                        HOME SCREEN
        ==================================================

        Select an option:

            (D)  Add Deposit
            (P)  Make Payment
            (L)  View Ledger
            (X)  Exit Application

        --------------------------------------------------
        Enter choice:
        """);

            char userChoice = input.nextLine().toUpperCase().charAt(0);

            switch (userChoice) {
                case 'D', 'd':
                    runAddDepositScreen();
                    break;
                case 'P', 'p':
                    runMakePaymentScreen();
                    break;
                case 'L', 'l':
                    runLedgerScreen();
                    break;
                case 'X', 'x':
                    System.out.println("Thanks for using our services!");
                    System.out.println("System exiting now.");
                    systemIsRunning = false;
                    return;
                default:
                    System.out.println("Wrong input. Try again.");
            }
        } while (systemIsRunning);

    }

    private static void runLedgerScreen() {



        do {
            System.out.println("""
        ==================================================
                        LEDGER MENU
        ==================================================

        Display Options:

            [A]  View All Entries
            [D]  View Deposits
            [P]  View Payments
            [R]  Reports
            [H]  Back to Home Screen

        --------------------------------------------------
        Enter choice: """);

            char userChoice = input.nextLine().charAt(0);

            switch (userChoice) {
                case 'A', 'a':
                    displayAllEntries();
                    break;
                case 'D', 'd':
                    displayDeposits();
                    break;
                case 'P', 'p':
                    displayPayments();
                    break;
                case 'R', 'r':
                    showReportsMenu();
                    break;
                case 'H', 'h':
                    return;
                default:
                    System.out.println("Wrong input. Try again.");
            }
        } while (true);
    }


    private static void showReportsMenu() {
        System.out.println("""
        ==================================================
                        REPORTS MENU
        ==================================================

        Choose a report to run:

            1)  Month To Date
            2)  Previous Month
            3)  Year To Date
            4)  Previous Year
            5)  Search by Vendor
            6)  Run a Custom Search

        --------------------------------------------------
        Enter choice:
        """);

        char userChoice = input.nextLine().charAt(0);

        switch (userChoice) {
            case '1':
                displayMonthToDateReport();
                break;
            case '2':
                displayPreviousMonthReport();
                break;
            case '3':
                displayYearToDateReport();
                break;
            case '4':
                displayPreviousYearReport();
                break;
            case '5':
                displayByVendor();
                break;
            case '6':
                displayCustomReport();
                break;
            default:
                System.out.println("Wrong input. Try again.");
        }


    }

    private static void displayCustomReport() {
        System.out.println("=== Custom Search === ");

        System.out.println("Enter start date (yyyy-MM-dd): ");
        String startDate = input.nextLine();

        System.out.println("Enter end date (yyyy-MM-dd): ");
        String endDate = input.nextLine();

        System.out.println("Enter transaction description: ");
        String description = input.nextLine();

        System.out.println("Enter vendor: ");
        String vendor = input.nextLine();

        System.out.println("Enter mininum amount: ");
        String minAmount = input.nextLine();

        System.out.println("Enter maximum amount: ");
        String maxAmount = input.nextLine();

        createCustomReport(startDate, endDate, description, vendor, minAmount, maxAmount);


    }


    private static void createCustomReport(String startDate, String endDate, String description, String vendor,
                                           String minAmount, String maxAmount) {

        double totalIncome = 0;
        double totalExpenses = 0;

        System.out.println("\n==========================================================================================================");
        System.out.println("                                      CUSTOM REPORT");
        System.out.println("==========================================================================================================");

        System.out.printf("%-15s %-10s %-30s %-30s %10s%n",
                "Date", "Time", "Description", "Vendor", "Amount");

        System.out.println("----------------------------------------------------------------------------------------------------------");

        Collections.sort(transactionsList);

        for (Transaction transaction : transactionsList) {

            boolean startDateMatches = true;
            boolean endDateMatches = true;
            boolean descriptionMatches = true;
            boolean vendorMatches = true;
            boolean minAmountMatches = true;
            boolean maxAmountMatches = true;

            // FIXED: inclusive date range
            if (!startDate.isEmpty() && transaction.getDate().isBefore(LocalDate.parse(startDate))) {
                startDateMatches = false;
            }

            if (!endDate.isEmpty() && transaction.getDate().isAfter(LocalDate.parse(endDate))) {
                endDateMatches = false;
            }

            if (!description.isEmpty() && !transaction.getDescription().equalsIgnoreCase(description)) {
                descriptionMatches = false;
            }

            if (!vendor.isEmpty() && !transaction.getVendor().equalsIgnoreCase(vendor)) {
                vendorMatches = false;
            }
//            if (!vendor.isEmpty() &&
//                    !transaction.getVendor().toLowerCase().contains(vendor.toLowerCase())) {
//                vendorMatches = false;
//            }

            if (!minAmount.isEmpty() && transaction.getAmount() < Double.parseDouble(minAmount)) {
                minAmountMatches = false;
            }

            if (!maxAmount.isEmpty() && transaction.getAmount() > Double.parseDouble(maxAmount)) {
                maxAmountMatches = false;
            }

            if (startDateMatches && endDateMatches && descriptionMatches &&
                    vendorMatches && minAmountMatches && maxAmountMatches) {

                double amount = transaction.getAmount();

                // Totals
                if (amount > 0) totalIncome += amount;
                else totalExpenses += amount;

                // Table row
                System.out.printf("%-15s %-10s %-30s %-30s %s$%10.2f\u001B[0m%n",
                        transaction.getDate(),
                        transaction.getTime(),
                        transaction.getDescription(),
                        transaction.getVendor(),
                        colorAmount(amount),
                        amount);
            }
        }

        double net = totalIncome + totalExpenses;

        // Footer
        System.out.println("----------------------------------------------------------------------------------------------------------");
        System.out.printf("%-85s $%10.2f%n", "Total Income:", totalIncome);
        System.out.printf("%-85s $%10.2f%n", "Total Expenses:", totalExpenses);
        System.out.printf("%-85s $%10.2f%n", "Net Balance:", net);
        System.out.println("==========================================================================================================\n");
    }



    private static void displayByVendor() {

        System.out.print("Enter the vendor you want a report on: ");
        String vendor = input.nextLine();

        double totalIncome = 0;
        double totalExpenses = 0;

        // Header
        System.out.println("\n==========================================================================================================");
        System.out.println("                              VENDOR STATEMENT (" + vendor.toUpperCase() + ")");
        System.out.println("==========================================================================================================");

        // SAME spacing as all your other reports
        System.out.printf("%-15s %-10s %-30s %-30s %10s%n",
                "Date", "Time", "Description", "Vendor", "Amount");

        System.out.println("----------------------------------------------------------------------------------------------------------");

        Collections.sort(transactionsList);

        for (Transaction transaction : transactionsList) {
            if (transaction.getVendor().equalsIgnoreCase(vendor)) {

                double amount = transaction.getAmount();

                // Totals
                if (amount > 0) totalIncome += amount;
                else totalExpenses += amount;

                // SAME formatting + color
                System.out.printf("%-15s %-10s %-30s %-30s %s$%10.2f\u001B[0m%n",
                        transaction.getDate(),
                        transaction.getTime(),
                        transaction.getDescription(),
                        transaction.getVendor(),
                        colorAmount(amount),
                        amount);
            }
        }

        double net = totalIncome + totalExpenses;

        // Footer
        System.out.println("----------------------------------------------------------------------------------------------------------");
        System.out.printf("%-85s $%10.2f%n", "Total Income:", totalIncome);
        System.out.printf("%-85s $%10.2f%n", "Total Expenses:", totalExpenses);
        System.out.printf("%-85s $%10.2f%n", "Net Balance:", net);
        System.out.println("==========================================================================================================\n");
    }



    private static void displayPreviousYearReport() {

        System.out.print("Enter the year you want a report on: ");
        int year = input.nextInt();
        input.nextLine(); // clear buffer

        double totalIncome = 0;
        double totalExpenses = 0;

        System.out.println("\n==========================================================================================================");
        System.out.println("                                      YEARLY STATEMENT (" + year + ")");
        System.out.println("==========================================================================================================");

        System.out.printf("%-15s %-10s %-30s %-30s %10s%n",
                "Date", "Time", "Description", "Vendor", "Amount");

        System.out.println("----------------------------------------------------------------------------------------------------------");

        Collections.sort(transactionsList);

        for (Transaction transaction : transactionsList) {
            if (transaction.getDate().getYear() == year) {

                double amount = transaction.getAmount();

                if (amount > 0) totalIncome += amount;
                else totalExpenses += amount;

                System.out.printf("%-15s %-10s %-30s %-30s %s$%10.2f\u001B[0m%n",
                        transaction.getDate(),
                        transaction.getTime(),
                        transaction.getDescription(),
                        transaction.getVendor(),
                        colorAmount(amount),
                        amount);
            }
        }

        double net = totalIncome + totalExpenses;

        System.out.println("----------------------------------------------------------------------------------------------------------");
        System.out.printf("%-85s $%10.2f%n", "Total Income:", totalIncome);
        System.out.printf("%-85s $%10.2f%n", "Total Expenses:", totalExpenses);
        System.out.printf("%-85s $%10.2f%n", "Net Balance:", net);
        System.out.println("==========================================================================================================\n");
    }


    private static void displayYearToDateReport() {

        LocalDate currentDate = LocalDate.now();
        int currentYear = currentDate.getYear();

        double totalIncome = 0;
        double totalExpenses = 0;

        // Header
        System.out.println("\n==========================================================================================================");
        System.out.println("                                      YEAR TO DATE STATEMENT");
        System.out.println("                                      January - " + currentDate);
        System.out.println("==========================================================================================================");

        // SAME spacing as your other table
        System.out.printf("%-15s %-10s %-30s %-30s %10s%n",
                "Date", "Time", "Description", "Vendor", "Amount");

        System.out.println("----------------------------------------------------------------------------------------------------------");

        Collections.sort(transactionsList);

        for (Transaction transaction : transactionsList) {
            if (transaction.getDate().getYear() == currentYear) {

                double amount = transaction.getAmount();

                // Totals
                if (amount > 0) totalIncome += amount;
                else totalExpenses += amount;

                // SAME formatting as your other loop
                System.out.printf("%-15s %-10s %-30s %-30s %s$%10.2f\u001B[0m%n",
                        transaction.getDate(),
                        transaction.getTime(),
                        transaction.getDescription(),
                        transaction.getVendor(),
                        colorAmount(amount),
                        amount);
            }
        }

        double net = totalIncome + totalExpenses;

        // Footer aligned with same width
        System.out.println("----------------------------------------------------------------------------------------------------------");
        System.out.printf("%-85s $%10.2f%n", "Total Income:", totalIncome);
        System.out.printf("%-85s $%10.2f%n", "Total Expenses:", totalExpenses);
        System.out.printf("%-85s $%10.2f%n", "Net Balance:", net);
        System.out.println("==========================================================================================================\n");
    }



    private static void displayPreviousMonthReport() {

        System.out.print("Enter the month (1-12): ");
        int month = input.nextInt();

        System.out.print("Enter the year: ");
        int year = input.nextInt();
        input.nextLine(); // clear buffer

        double totalIncome = 0;
        double totalExpenses = 0;

        // Header
        System.out.println("\n==========================================================================================================");
        System.out.println("                          MONTHLY STATEMENT (" + month + "/" + year + ")");
        System.out.println("==========================================================================================================");

        // SAME spacing as your other tables
        System.out.printf("%-15s %-10s %-30s %-30s %10s%n",
                "Date", "Time", "Description", "Vendor", "Amount");

        System.out.println("----------------------------------------------------------------------------------------------------------");

        Collections.sort(transactionsList);

        for (Transaction transaction : transactionsList) {
            if (transaction.getDate().getYear() == year &&
                    transaction.getDate().getMonthValue() == month) {

                double amount = transaction.getAmount();

                // Totals
                if (amount > 0) totalIncome += amount;
                else totalExpenses += amount;

                // SAME formatting
                System.out.printf("%-15s %-10s %-30s %-30s %s$%10.2f\u001B[0m%n",
                        transaction.getDate(),
                        transaction.getTime(),
                        transaction.getDescription(),
                        transaction.getVendor(),
                        colorAmount(amount),
                        amount);
            }
        }

        double net = totalIncome + totalExpenses;

        // Footer
        System.out.println("----------------------------------------------------------------------------------------------------------");
        System.out.printf("%-85s $%10.2f%n", "Total Income:", totalIncome);
        System.out.printf("%-85s $%10.2f%n", "Total Expenses:", totalExpenses);
        System.out.printf("%-85s $%10.2f%n", "Net Balance:", net);
        System.out.println("==========================================================================================================\n");
    }



    private static void displayMonthToDateReport() {

        LocalDate currentDate = LocalDate.now();
        int currentMonth = currentDate.getMonthValue();
        int currentYear  = currentDate.getYear();

        double totalIncome = 0;
        double totalExpenses = 0;

        // Header
        System.out.println("\n==========================================================================================================");
        System.out.println("                          MONTH TO DATE STATEMENT");
        System.out.println("                          " + currentDate.getMonth() + " " + currentYear);
        System.out.println("==========================================================================================================");

        // SAME spacing as your other tables
        System.out.printf("%-15s %-10s %-30s %-30s %10s%n",
                "Date", "Time", "Description", "Vendor", "Amount");

        System.out.println("----------------------------------------------------------------------------------------------------------");

        Collections.sort(transactionsList);

        for (Transaction transaction : transactionsList) {
            if (transaction.getDate().getYear() == currentYear &&
                    transaction.getDate().getMonthValue() == currentMonth) {

                double amount = transaction.getAmount();


                if (amount > 0) totalIncome += amount;
                else totalExpenses += amount;


                System.out.printf("%-15s %-10s %-30s %-30s %s$%10.2f\u001B[0m%n",
                        transaction.getDate(),
                        transaction.getTime(),
                        transaction.getDescription(),
                        transaction.getVendor(),
                        colorAmount(amount),
                        amount);
            }
        }

        double net = totalIncome + totalExpenses;

        // Footer
        System.out.println("----------------------------------------------------------------------------------------------------------");
        System.out.printf("%-85s $%10.2f%n", "Total Income:", totalIncome);
        System.out.printf("%-85s $%10.2f%n", "Total Expenses:", totalExpenses);
        System.out.printf("%-85s $%10.2f%n", "Net Balance:", net);
        System.out.println("==========================================================================================================\n");
    }


    private static void displayPayments() {
        Collections.sort(transactionsList);

        System.out.printf("%-15s %-10s %-30s %-30s %10s%n",
                "Date", "Time", "Description", "Vendor", "Amount");

        System.out.println("----------------------------------------------------------------------------------------------------------");


        for (Transaction transaction : transactionsList) {
            if (transaction.getAmount() < 0) {

                double amount = transaction.getAmount();

                System.out.printf("%-15s %-10s %-30s %-30s %s$%10.2f\u001B[0m%n",
                        transaction.getDate(),
                        transaction.getTime(),
                        transaction.getDescription(),
                        transaction.getVendor(),
                        colorAmount(amount),
                        amount);
            }
        }
    }

    private static void displayDeposits() {
        Collections.sort(transactionsList);


        System.out.printf("%-15s %-10s %-30s %-30s %10s%n",
                "Date", "Time", "Description", "Vendor", "Amount");

        System.out.println("----------------------------------------------------------------------------------------------------------");


        for (Transaction transaction : transactionsList) {
            if (transaction.getAmount() > 0) {

                double amount = transaction.getAmount();

                System.out.printf("%-15s %-10s %-30s %-30s %s$%10.2f\u001B[0m%n",
                        transaction.getDate(),
                        transaction.getTime(),
                        transaction.getDescription(),
                        transaction.getVendor(),
                        colorAmount(amount),
                        amount);
            }
        }

    }

    private static void displayAllEntries(){
        Collections.sort(transactionsList);

        System.out.printf("%-15s %-10s %-30s %-30s %10s%n",
                "Date", "Time", "Description", "Vendor", "Amount");

        System.out.println("----------------------------------------------------------------------------------------------------------");



        for (Transaction transaction : transactionsList) {

            double amount = transaction.getAmount();

            System.out.printf("%-15s %-10s %-30s %-30s %s$%10.2f\u001B[0m%n",
                    transaction.getDate(),
                    transaction.getTime(),
                    transaction.getDescription(),
                    transaction.getVendor(),
                    colorAmount(amount),
                    amount);
        }

    }

    private static String colorAmount(double amount) {
        if (amount > 0) return "\u001B[32m"; // green
        if (amount < 0) return "\u001B[31m"; // red
        return "\u001B[0m"; // default
    }



    private static void runAddDepositScreen() {
        boolean invalidInput = false;
        double deposit = 0;

        while(!invalidInput){
            try {
                System.out.println("Enter the amount to deposit: ");
                deposit = Math.abs(input.nextDouble());
                input.nextLine();

                invalidInput = true;
            } catch (InputMismatchException e){
                System.out.println("ERROR: Invalid input. Please enter a valid number to be recorded as amount");
                input.nextLine();
            }

        }


        System.out.println("Enter the name of source of income: ");
        String vendor = input.next();

        input.nextLine();
        System.out.println("Enter income description: ");
        String description = input.nextLine();

        LocalTime timeOfTranasaction = getCurrentLocalTime();
        LocalDate dateOfTransaction = LocalDate.from(LocalDateTime.now());


        Transaction currentTransaction = new Transaction();
        currentTransaction.setDate(dateOfTransaction);
        currentTransaction.setTime(timeOfTranasaction);
        currentTransaction.setDescription(description);
        currentTransaction.setVendor(vendor);
        currentTransaction.setAmount(deposit);
        transactionsList.add(currentTransaction);

        addTransactionToFile(currentTransaction);


    }

    private static void runMakePaymentScreen() {
        double payment = 0;
        boolean validInput = false;

        while(!validInput) {
            try {
                System.out.println("Enter the amount to pay: ");
                payment = Math.abs(input.nextDouble()) * -1;
                input.nextLine();

                validInput = true;

            } catch (InputMismatchException e) {
                System.out.println("ERROR: Invalid input. Please enter a valid number to be recorded as amount");
                input.nextLine();
            }
        }


        System.out.println("Enter the name of the vendor: ");
        String vendor = input.next();

        input.nextLine();
        System.out.println("Enter expense description: ");
        String description = input.nextLine();

        LocalTime timeOfTranasaction = getCurrentLocalTime();
        LocalDate dateOfTransaction = LocalDate.from(LocalDateTime.now());


        Transaction currentTransaction = new Transaction();
        currentTransaction.setDate(dateOfTransaction);
        currentTransaction.setTime(timeOfTranasaction);
        currentTransaction.setDescription(description);
        currentTransaction.setVendor(vendor);
        currentTransaction.setAmount(payment);
        transactionsList.add(currentTransaction);

        addTransactionToFile(currentTransaction);
    }

    private static void addTransactionToFile(Transaction currentTransaction) {
        try {
            FileWriter fileWriter = new FileWriter("transactions.csv", true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);


            bufferedWriter.write(currentTransaction.getDate() + "|" + currentTransaction.getTime() + "|" +
                    currentTransaction.getDescription() + "|" + currentTransaction.getVendor() + "|" +
                    currentTransaction.getAmount() + "\n");


            bufferedWriter.close();
        }
        catch (IOException e){
            System.out.println("ERROR: An unexpected error occurred");
            e.printStackTrace();
        }
    }





    }



