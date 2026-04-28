package com.pluralsight;

import java.io.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.time.*;

import static java.lang.Double.*;


public class App  {
    static Scanner input = new Scanner(System.in);
    static ArrayList<Transaction> transactionsList = new ArrayList<>();


    public static void main(String[] args) {
        boolean systemIsRunning = true;
        LocalDate currentDate = LocalDate.from(LocalDateTime.now());


        System.out.println(getCurrentLocalTime());

        try {
            extractFile("transactions.csv");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        runHomeScreen();


    }

    private static LocalTime getCurrentLocalTime() {
        LocalTime currentTime = LocalTime.from(LocalDateTime.now());


        DateTimeFormatter formattedTime = DateTimeFormatter.ofPattern("HH:mm:ss");
        String formattedTime1 = currentTime.format(formattedTime);
        LocalTime fullyFormattedTime = LocalTime.parse(formattedTime1);
        return fullyFormattedTime;
    }

    private static void extractFile(String fileName) throws IOException {
        FileReader fileReader = new FileReader(fileName);
        BufferedReader bufferedReader = new BufferedReader(fileReader);



        String input;
        bufferedReader.readLine();

        while((input = bufferedReader.readLine()) != null) {
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
    }

    private static void runHomeScreen() {
        boolean systemIsRunning = true;


        do {
            System.out.println("=== Menu ===");
            System.out.println("(D) Add Deposit\n" +
                    "(P) Make Payment\n" +
                    "(L) Ledger\n" +
                    "(X) Exit - closes out of the application");
            char userChoice = input.next().charAt(0);
            input.nextLine();

            switch (userChoice) {
                case 'D', 'd':
                    runAddDepositScreen();
                    break;
                case 'P', 'p':
                    runMakePaymentScreen();
                    break;
                case 'L', 'l':
                    runLedgerScreen();
                    System.out.println("Thanks for using our services!");
                    System.out.println("System exiting now.");
                    systemIsRunning = false;
                    return;
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
            System.out.println("=== Ledger Menu ===");
            System.out.println("Display:\n" +
                    "(A) All Entires\n" +
                    "(D) Deposits\n" +
                    "(P) Payments\n" +
                    "(R) Reports\n" +
                    "(H) Go back to Home Screen");
            char userChoice = input.next().charAt(0);
            input.nextLine();

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
                    showReports();
                    break;
                case 'H', 'h':
                    runHomeScreen();
                    break;
                default:
                    System.out.println("Wrong input. Try again.");
            }
        } while (true);
    }


    private static void showReports() {
        System.out.println("=== Choose a type of report to run: ");
        System.out.println(" 1) Month To Date\n" +
                " 2) Previous Month\n" +
                " 3) Year To Date\n" +
                " 4) Previous Year\n" +
                " 5) Search by Vendor\n" +
                " 6) Run a custom search");

        int userChoice = input.nextInt();
        input.nextLine();

        switch (userChoice) {
            case 1:
                displayMonthToDateReport();
                break;
            case 2:
                displayPreviousMonthReport();
                break;
            case 3:
                displayYearToDateReport();
                break;
            case 4:
                displayPreviousYearReport();
                break;
            case 5:
                displayByVendor();
                break;
            case 6:
                displayCutsomReport();
                break;
            default:
                System.out.println("Wrong input. Try again.");
        }


    }

    private static void displayCutsomReport() {
        System.out.println("=== Custom Search === ");

        System.out.println("Enter start date (yyyy-MM-dd): ");
        String startDate = input.nextLine();

        System.out.println("Enter end date (yyyy-MM-dd): ");
        String endDate = input.nextLine();

        System.out.println("Enter transaction description: ");
        String description = input.nextLine();

        System.out.println("Enter vendor: ");
        String vendor = input.nextLine();

        System.out.println("Enter amount: ");
        String amount = input.nextLine();

        createCustomReport(startDate, endDate, description, vendor, amount);


    }

    private static void createCustomReport(String startDate, String endDate, String description, String vendor,
                                           String amount) {




        Collections.sort(transactionsList);

        for(Transaction transaction: transactionsList){
            boolean startDateMatches = true;
            boolean endDateMatches = true;
            boolean descriptionMatches = true;
            boolean vendorMatches = true;
            boolean amountMatches = true;

            if(!startDate.isEmpty() && !transaction.getDate().isAfter(LocalDate.parse(startDate))){
                startDateMatches = false;
            }

            if(!endDate.isEmpty() && !transaction.getDate().isBefore(LocalDate.parse(endDate))){
                endDateMatches = false;
            }
            if(!description.isEmpty() && !transaction.getDescription().equalsIgnoreCase(description)){
                amountMatches = false;
            }
            if(!vendor.isEmpty() && !transaction.getVendor().equalsIgnoreCase(vendor)){
                vendorMatches = false;
            }
            if(!amount.isEmpty() && transaction.getAmount() != Double.parseDouble(amount)){
                descriptionMatches = false;
            }



            if (startDateMatches && endDateMatches && descriptionMatches && vendorMatches && amountMatches){
                System.out.println("[Date: " + transaction.getDate() + ", time: " + transaction.getTime()
                        + ", description: " + transaction.getDescription() + ", vendor: " + transaction.getVendor()
                        + ", amount: $" + transaction.getAmount() + "]");
            }

        }
        System.out.println("My custom report");



    }

//    1. On the reports screen add another option for a custom search. Prompt the user
//for search values for all ledger entry properties.
//o 6) Custom Search - prompt the user for the following search values.
//        ▪ Start Date
//▪ End Date
//▪ Description
//▪ Vendor
//▪ Amount
//o If the user enters a value for a field you should filter on that field
//o If the user does not enter a value, you should not filter on that field

    private static void displayByVendor() {

        System.out.println("Enter the vendor you want a report on: ");
        String vendor = input.nextLine();


        Collections.sort(transactionsList);
        for(Transaction transaction: transactionsList){
            if (transaction.getVendor().equalsIgnoreCase(vendor)) {
                System.out.println("[Date: " + transaction.getDate() + ", time: " + transaction.getTime()
                        + ", description: " + transaction.getDescription() + ", vendor: " + transaction.getVendor()
                        + ", amount: $" + transaction.getAmount() + "]");
            }
        }
    }

    private static void displayPreviousYearReport() {

        System.out.println("Enter the year you want a report on: ");
        int year = input.nextInt();

        LocalTime currentTime = getCurrentLocalTime();
        LocalDate currentDate = LocalDate.from(LocalDateTime.now());

        int currentMonth = currentDate.getMonthValue();
        int currentYear  = currentDate.getYear();


        Collections.sort(transactionsList);
        for(Transaction transaction: transactionsList){
            if (transaction.getDate().getYear() == year ) {
                System.out.println("[Date: " + transaction.getDate() + ", time: " + transaction.getTime()
                        + ", description: " + transaction.getDescription() + ", vendor: " + transaction.getVendor()
                        + ", amount: $" + transaction.getAmount() + "]");
            }
        }
    }

    private static void displayYearToDateReport() {
        LocalTime currentTime = getCurrentLocalTime();
        LocalDate currentDate = LocalDate.from(LocalDateTime.now());

        int currentMonth = currentDate.getMonthValue();
        int currentYear  = currentDate.getYear();


        Collections.sort(transactionsList);
        for(Transaction transaction: transactionsList){
            if (transaction.getDate().getYear() == currentYear) {
                System.out.println("[Date: " + transaction.getDate() + ", time: " + transaction.getTime()
                        + ", description: " + transaction.getDescription() + ", vendor: " + transaction.getVendor()
                        + ", amount: $" + transaction.getAmount() + "]");
            }
        }


    }

    private static void displayPreviousMonthReport() {
        System.out.println("Enter the month you want a report on: ");
        int month = input.nextInt();

        System.out.println("Enter the year you want a report on: ");
        int year = input.nextInt();

        LocalTime currentTime = getCurrentLocalTime();
        LocalDate currentDate = LocalDate.from(LocalDateTime.now());

        int currentMonth = currentDate.getMonthValue();
        int currentYear  = currentDate.getYear();


        Collections.sort(transactionsList);
        for(Transaction transaction: transactionsList){
            if (transaction.getDate().getYear() == year && transaction.getDate().getMonthValue() == month) {
                System.out.println("[Date: " + transaction.getDate() + ", time: " + transaction.getTime()
                        + ", description: " + transaction.getDescription() + ", vendor: " + transaction.getVendor()
                        + ", amount: $" + transaction.getAmount() + "]");
            }
        }
    }

    private static void displayMonthToDateReport() {
        LocalTime currentTime = getCurrentLocalTime();
        LocalDate currentDate = LocalDate.from(LocalDateTime.now());

        int currentMonth = currentDate.getMonthValue();
        int currentYear  = currentDate.getYear();


        Collections.sort(transactionsList);
        for(Transaction transaction: transactionsList){
            if (transaction.getDate().getYear() == currentYear && transaction.getDate().getMonthValue() == currentMonth) {
                System.out.println("[Date: " + transaction.getDate() + ", time: " + transaction.getTime()
                        + ", description: " + transaction.getDescription() + ", vendor: " + transaction.getVendor()
                        + ", amount: $" + transaction.getAmount() + "]");
            }
        }



        System.out.println("=== Month To Date Report ====");
        double totalPayments = 0;
        double totalDeposits = 0;



    }


    private static void displayPayments() {
        Collections.sort(transactionsList);
        for(Transaction transaction: transactionsList){
            if (transaction.getAmount() < 0) {
                System.out.println("[Date: " + transaction.getDate() + ", time: " + transaction.getTime()
                        + ", description: " + transaction.getDescription() + ", vendor: " + transaction.getVendor()
                        + ", amount: $" + transaction.getAmount() + "]");
            }
        }
    }

    private static void displayDeposits() {
        Collections.sort(transactionsList);

        for(Transaction transaction: transactionsList){
            if (transaction.getAmount() > 0) {
                System.out.println("[Date: " + transaction.getDate() + ", time: " + transaction.getTime()
                        + ", description: " + transaction.getDescription() + ", vendor: " + transaction.getVendor()
                        + ", amount: $" + transaction.getAmount() + "]");
            }
        }

    }

    private static void displayAllEntries(){
        Collections.sort(transactionsList);
        for(Transaction transaction: transactionsList){
            System.out.println("[Date: " + transaction.getDate() + ", time: " + transaction.getTime()
                    + ", description: " + transaction.getDescription() + ", vendor: " + transaction.getVendor()
                    + ", amount: $" + transaction.getAmount() + "]");
        }

    }





    //• Ledger - All entries should show the newest entries first
//o A) All - Display all entries
//o D) Deposits - Display only the entries that are deposits into the account
//o P) Payments - Display only the negative entries (or payments)

//▪ 0) Back - go back to the Ledger page
//o H) Home - go back to the home page

    private static void runAddDepositScreen() {
        System.out.println("Enter the amount to deposit: ");
        double payment = input.nextDouble();

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
        currentTransaction.setAmount(payment);
        transactionsList.add(currentTransaction);

        addTransactionToFile(currentTransaction);


    }

    private static void runMakePaymentScreen() {
        System.out.println("Enter the amount to pay: ");
        double payment = input.nextDouble() * -1;

        System.out.println("Enter the name of the vendor: ");
        String vendor = input.next();

        input.nextLine();
        System.out.println("Enter payment description: ");
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









//Description
//In this project, you will use what you have learned about Java programming to create a
//CLI application. With this application you can track all financial transactions for a
//business or for personal use.
//All transactions in the application should be read from and saved to a transaction file
//named transactions.csv. Each transaction should be saved as a single line with
//the following format.
//        date|time|description|vendor|amount
//2023-04-15|10:13:25|ergonomic keyboard|Amazon|-89.50
//        2023-04-15|11:15:00|Invoice 1001 paid|Joe|1500.00
//        3

//Application Requirements
//Your application must include several screens with the listed features in order to be
//considered complete:
//        • Home Screen
//o The home screen should give the user the following options. The
//application should continue to run until the user chooses to exit.
//        ▪ D) Add Deposit - prompt user for the deposit information and save it
//to the csv file
//▪ P) Make Payment (Debit) - prompt user for the debit information
//and save it to the csv file
//▪ L) Ledger - display the ledger screen
//▪ X) Exit - exit the application






//4
//Bonus and Presentations
//Challenge Yourself
//If you have time and want to challenge yourself, consider the following:
//        1. On the reports screen add another option for a custom search. Prompt the user
//for search values for all ledger entry properties.
//o 6) Custom Search - prompt the user for the following search values.
//        ▪ Start Date
//▪ End Date
//▪ Description
//▪ Vendor
//▪ Amount
//o If the user enters a value for a field you should filter on that field
//o If the user does not enter a value, you should not filter on that field


//2. Customize your app
//o Trick out your console app by giving it a unique app name, starter screen
//o You can even make your transactions look like it’s a part of your unique app
//o Some examples: Restaurant Sales/Purchases, Record Store Sales/Purchases,
//Clothing Store Sales/Purchases


//Other Requirements
//Your project must also meet the following requirements:
//        • Your code must be in a public GitHub repository
//• The repository must contain an appropriate Git commit history
//o At a minimum, you should have a commit for each meaningful piece of
//work completed
//• It must contain an informative README file that:
//o Describes your project
//o Includes description for how to run the project
//Class Demonstrations
//Each student will be given 10 minutes to demonstrate their project to the class on
//"project demonstration day". During this time, you will:
//        • Present your application - run through the different screens and scenarios
//5
//        • Describe / show one interesting piece of code that you wrote
//• Answer questions from the audience if time permit
