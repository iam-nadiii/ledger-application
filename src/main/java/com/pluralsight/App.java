package com.pluralsight;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.security.spec.RSAOtherPrimeInfo;
import java.util.ArrayList;
import java.util.Scanner;




public class App {
    static Scanner input = new Scanner(System.in);
    static ArrayList<Transaction> transactionsList = new ArrayList<>();

    public static void main(String[] args) {
        boolean systemIsRunning = true;

        System.out.println("Love");
        runHomeScreen();


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
                    runDepositScreen();
                    break;
                case 'P', 'p':
                    runPaymentScreen();
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

    }

    private static void displayPayments() {
        try {
            extractFile("transactions.csv");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        for(int i = transactionsList.size() -1; i >= 0; i--){
            if (transactionsList.get(i).getAmount() < 0) {
                System.out.println("[Date: " + transactionsList.get(i).getDate() + ", time: " + transactionsList.get(i).getTime()
                        + ", description: " + transactionsList.get(i).getDescription() + ", vendor: " + transactionsList.get(i).getVendor()
                        + ", amount: $" + transactionsList.get(i).getAmount() + "]");
            }
        }
    }

    private static void displayDeposits() {
        try {
            extractFile("transactions.csv");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        for(int i = transactionsList.size() -1; i >= 0; i--){
            if (transactionsList.get(i).getAmount() > 0) {
                System.out.println("[Date: " + transactionsList.get(i).getDate() + ", time: " + transactionsList.get(i).getTime()
                        + ", description: " + transactionsList.get(i).getDescription() + ", vendor: " + transactionsList.get(i).getVendor()
                        + ", amount: $" + transactionsList.get(i).getAmount() + "]");
            }
        }

    }

    private static void displayAllEntries(){

        try {
            extractFile("transactions.csv");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        for(int i = transactionsList.size() -1; i >= 0; i--){
            System.out.println("[Date: " + transactionsList.get(i).getDate() + ", time: " + transactionsList.get(i).getTime()
                    + ", description: " + transactionsList.get(i).getDescription() + ", vendor: " + transactionsList.get(i).getVendor()
                    + ", amount: $" + transactionsList.get(i).getAmount() + "]");
        }

    }





    //• Ledger - All entries should show the newest entries first
//o A) All - Display all entries
//o D) Deposits - Display only the entries that are deposits into the account
//o P) Payments - Display only the negative entries (or payments)
//o R) Reports - A new screen that allows the user to run pre-defined reports or
//to run a custom search
//▪ 1) Month To Date
//▪ 2) Previous Month
//▪ 3) Year To Date
//▪ 4) Previous Year
//▪ 5) Search by Vendor - prompt the user for the vendor name and
//display all entries for that vendor
//▪ 0) Back - go back to the Ledger page
//o H) Home - go back to the home page

    private static void runDepositScreen() {
    }

    private static void runPaymentScreen() {
    }
\


    private static void extractFile(String fileName) throws IOException {
            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);



            String input;
            bufferedReader.readLine();

            while((input = bufferedReader.readLine()) != null) {
                String[] transactionAttributes = input.split("\\|");
                Transaction currentTransaction = new Transaction();
                currentTransaction.setDate(transactionAttributes[0]);
                currentTransaction.setTime(transactionAttributes[1]);
                currentTransaction.setDescription(transactionAttributes[2]);
                currentTransaction.setVendor(transactionAttributes[3]);
                currentTransaction.setAmount(Double.parseDouble(transactionAttributes[4]));
                transactionsList.add(currentTransaction);
            }

            bufferedReader.close();
        }
    }


    //        • Home Screen
//o The home screen should give the user the following options. The
//application should continue to run until the user chooses to exit.
//        ▪ D) Add Deposit - prompt user for the deposit information and save it
//to the csv file
//▪ P) Make Payment (Debit) - prompt user for the debit information
//and save it to the csv file
//▪ L) Ledger - display the ledger screen
//▪ X) Exit - exit the application






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
