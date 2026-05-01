package com.pluralsight;

import java.io.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.time.*;

import static java.lang.Double.*;


public class App  {

    // Scanner used for all user input
    static Scanner scanner = new Scanner(System.in);

    // Stores all transactions loaded from file and added during runtime
    static ArrayList<Transaction> transactions = new ArrayList<>();

    // Stores all past searches for reuse and persistence
    static ArrayList<Search> searches = new ArrayList<>();



    public static void main(String[] args) {

        // Print current time when application starts
        System.out.println(getCurrentLocalTime());

        // Load existing data from CSV files into memory
        loadTransactionsFromFile("transactions.csv");
        loadSearchHistoryFromFile("searches.csv");

        // Start main application UI
        runHomeScreen();
    }

    private static LocalTime getCurrentLocalTime() {
        LocalTime currentTime = LocalTime.from(LocalDateTime.now());


        DateTimeFormatter formattedTime = DateTimeFormatter.ofPattern("HH:mm:ss");
        String formattedTime1 = currentTime.format(formattedTime);
        LocalTime fullyFormattedTime = LocalTime.parse(formattedTime1);
        return fullyFormattedTime;
    }

    private static void loadTransactionsFromFile(String fileName) {

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
                transactions.add(currentTransaction);
            }
            Collections.sort(transactions);

            bufferedReader.close();
        } catch (IOException e){
            System.out.println("ERROR: Unable to load transaction data from file.");
            System.out.println("Please make sure 'transactions.csv' exists and is not corrupted.");
            e.printStackTrace();
        }
    }

    private static void runHomeScreen() {


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

            char userChoice = scanner.nextLine().toUpperCase().charAt(0);

            switch (userChoice) {
                case 'D':
                    runAddDepositScreen();
                    break;
                case 'P':
                    runMakePaymentScreen();
                    break;
                case 'L':
                    runLedgerScreen();
                    break;
                case 'X':
                    System.out.println("Thanks for using our services!");
                    System.out.println("System exiting now.");
                    return;
                default:
                    System.out.println("Wrong input. Try again.");
            }
        } while (true);

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

            char userChoice = scanner.nextLine().charAt(0);

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
            7)  Display Past Searches
            0)  Back to the Ledger Page

        --------------------------------------------------
        Enter choice:
        """);

        char userChoice = scanner.nextLine().charAt(0);

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
            case '7':
                displayPastSearches();
                break;
            case '0':
                return;
            default:
                System.out.println("Wrong input. Try again.");
        }


    }

    private static void displayPastSearches() {
        int totalWidth = 121;
        String border = "═".repeat(totalWidth);
        String separator = "-".repeat(totalWidth);


        String title = "📊 PAST SEARCHES";
        int padding = (totalWidth - title.length()) / 2;

        System.out.println("\n" + border);
        System.out.println(" ".repeat(padding) + title);
        System.out.println(border);


        System.out.printf("\u001B[34m%-5s\u001B[0m %-14s %-12s %-12s %-12s %-20s %-18s %-10s %-10s%n",
                "ID", "Search-Date", "Search-Time", "Start", "End", "Description", "Vendor", "Min", "Max");

        System.out.println(separator);

        int index = 1;

        for (Search search : searches) {
            System.out.printf("\u001B[35m%-5d\u001B[0m %-14s %-12s %-12s %-12s %-20s %-18s %-10s %-10s%n",
                    index++,
                    search.getSearchDate(),
                    search.getSearchTime(),
                    search.getStartDate(),
                    search.getEndDate(),
                    search.getDescription(),
                    search.getVendor(),
                    search.getMinAmount(),
                    search.getMaxAmount()
            );
        }


        System.out.println("-------------------------------------------------------------------------------------------------------------------------");
        System.out.print("\n👉 Enter the \u001B[35mID\u001B[0m of the search you want to reuse: ");
        int searchID = Integer.parseInt(scanner.nextLine());

        int searchIndex = searchID - 1;


        Search currentSearch = searches.get(searchIndex);

        String startDate = currentSearch.getStartDate();
        String endDate = currentSearch.getEndDate();
        String description = currentSearch.getDescription();
        String vendor = currentSearch.getVendor();
        String minAmount = currentSearch.getMinAmount();
        String maxAmount = currentSearch.getMaxAmount();

        displayFilteredTransactionsReport(startDate, endDate, description, vendor, minAmount, maxAmount);
        recordCurrentSearch(startDate, endDate, maxAmount, minAmount, description, vendor);

    }

    private static void displayCustomReport() {
        System.out.println("=== Custom Search === ");

        System.out.println("Enter start date (yyyy-MM-dd): ");
        String startDate = scanner.nextLine();

        System.out.println("Enter end date (yyyy-MM-dd): ");
        String endDate = scanner.nextLine();

        System.out.println("Enter transaction description: ");
        String description = scanner.nextLine();

        System.out.println("Enter vendor: ");
        String vendor = scanner.nextLine();

        System.out.println("Enter minimum amount: ");
        String minAmount = scanner.nextLine();

        System.out.println("Enter maximum amount: ");
        String maxAmount = scanner.nextLine();

        if (!startDate.equals("N/A") && !startDate.isEmpty()
                && !endDate.equals("N/A") && !endDate.isEmpty()) {

            if (LocalDate.parse(startDate).isAfter(LocalDate.parse(endDate))) {
                String tempDate = startDate;
                startDate = endDate;
                endDate = tempDate;
            }
        }

        displayFilteredTransactionsReport(startDate, endDate, description, vendor, minAmount, maxAmount);

        recordCurrentSearch(startDate, endDate, maxAmount, minAmount, description, vendor);

    }

    private static void recordCurrentSearch(String startDate, String endDate, String maxAmount, String minAmount, String description, String vendor) {
        Search currentSearch = new Search();

        currentSearch.setSearchTime(getCurrentLocalTime());
        currentSearch.setSearchDate(LocalDate.from(LocalDateTime.now()));
        currentSearch.setStartDate(startDate);
        currentSearch.setEndDate(endDate);
        currentSearch.setMaxAmount(maxAmount);
        currentSearch.setMinAmount(minAmount);
        currentSearch.setDescription(description);
        currentSearch.setVendor(vendor);

        searches.add(currentSearch);
        Collections.sort(searches);

        addSearchToFile(currentSearch);
    }

    private static void addSearchToFile(Search currentSearch) {
        String searchedStartDate = currentSearch.getStartDate();
        String searchedEndDate = currentSearch.getEndDate();
        String searchedMaxAmount = currentSearch.getMaxAmount();
        String searchedMinAmount = currentSearch.getMinAmount();
        String searchedDescription = currentSearch.getDescription();
        String searchedVendor = currentSearch.getVendor();

        searchedStartDate = searchedStartDate.isEmpty()?  "N/A": searchedStartDate;
        searchedEndDate = searchedEndDate.isEmpty()?  "N/A": searchedEndDate;
        searchedMaxAmount = searchedMaxAmount.isEmpty()?  "N/A": searchedMaxAmount;
        searchedMinAmount = searchedMinAmount.isEmpty()?  "N/A": searchedMinAmount;
        searchedDescription = searchedDescription.isEmpty()?  "N/A": searchedDescription;
        searchedVendor = searchedVendor.isEmpty()?  "N/A": searchedVendor;

        try{
            FileWriter fileWriter = new FileWriter("searches.csv", true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            bufferedWriter.write("\n" + currentSearch.getSearchDate() +  "|" + currentSearch.getSearchTime() + "|" +
                    searchedStartDate + "|" + searchedEndDate + "|" + searchedDescription
            + "|" + searchedVendor + "|" + searchedMinAmount + "|" + searchedMaxAmount);
            bufferedWriter.close();

        } catch (IOException e){
            System.out.println("ERROR: Unable to write current search into file.");
            System.out.println("Please make sure 'searches.csv' exists and is not corrupted.");
            e.printStackTrace();
        }
    }


    private static void loadSearchHistoryFromFile(String fileName){
        try {
            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);


            String input;
            bufferedReader.readLine();

            while ((input = bufferedReader.readLine()) != null) {
                String[] searchAttributes = input.split("\\|");
                Search currentSearch = new Search();
                currentSearch.setSearchDate(LocalDate.parse(searchAttributes[0]));
                currentSearch.setSearchTime(LocalTime.parse(searchAttributes[1]));
                currentSearch.setStartDate(searchAttributes[2]);
                currentSearch.setEndDate(searchAttributes[3]);
                currentSearch.setDescription(searchAttributes[4]);
                currentSearch.setVendor(searchAttributes[5]);
                currentSearch.setMinAmount(searchAttributes[6]);
                currentSearch.setMaxAmount(searchAttributes[7]);

                searches.add(currentSearch);


            }
            Collections.sort(searches);

            bufferedReader.close();
        } catch (IOException e){
            System.out.println("ERROR: Unable to load search history from file.");
            System.out.println("Please make sure 'searches.csv' exists and is not corrupted.");
            e.printStackTrace();
        }


    }


    private static void displayFilteredTransactionsReport(String startDate, String endDate, String description, String vendor,
                                                          String minAmount, String maxAmount) {

        double totalIncome = 0;
        double totalExpenses = 0;


        System.out.println("\n==========================================================================================================");
        System.out.println("                                      CUSTOM REPORT");
        System.out.println("==========================================================================================================");

        System.out.printf("%-15s %-10s %-30s %-30s %10s%n",
                "Date", "Time", "Description", "Vendor", "Amount");

        System.out.println("----------------------------------------------------------------------------------------------------------");

        Collections.sort(transactions);

        for (Transaction transaction : transactions) {

            boolean startDateMatches = true;
            boolean endDateMatches = true;
            boolean descriptionMatches = true;
            boolean vendorMatches = true;
            boolean minAmountMatches = true;
            boolean maxAmountMatches = true;

            // FIXED: inclusive date range
            if (!startDate.equals("N/A") && !startDate.isEmpty() && transaction.getDate().isBefore(LocalDate.parse(startDate))) {
                startDateMatches = false;
            }

            if (!endDate.equals("N/A") && !endDate.isEmpty() && transaction.getDate().isAfter(LocalDate.parse(endDate))) {
                endDateMatches = false;
            }

            if (!description.equals("N/A") && !description.isEmpty() && !transaction.getDescription().equalsIgnoreCase(description)) {
                descriptionMatches = false;
            }

            if (!vendor.equals("N/A") && !vendor.isEmpty() &&
                    !transaction.getVendor().toLowerCase().contains(vendor.toLowerCase())) {
                vendorMatches = false;
            }

            if (!minAmount.equals("N/A") && !minAmount.isEmpty() && transaction.getAmount() < Double.parseDouble(minAmount)) {
                minAmountMatches = false;
            }

            if (!maxAmount.equals("N/A") && !maxAmount.isEmpty() && transaction.getAmount() > Double.parseDouble(maxAmount)) {
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
        String vendor = scanner.nextLine();

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

        Collections.sort(transactions);

        for (Transaction transaction : transactions) {
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
        int year = scanner.nextInt();
        scanner.nextLine(); // clear buffer

        double totalIncome = 0;
        double totalExpenses = 0;

        System.out.println("\n==========================================================================================================");
        System.out.println("                                      YEARLY STATEMENT (" + year + ")");
        System.out.println("==========================================================================================================");

        System.out.printf("%-15s %-10s %-30s %-30s %10s%n",
                "Date", "Time", "Description", "Vendor", "Amount");

        System.out.println("----------------------------------------------------------------------------------------------------------");

        Collections.sort(transactions);

        for (Transaction transaction : transactions) {
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

        Collections.sort(transactions);

        for (Transaction transaction : transactions) {
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
        int month = scanner.nextInt();

        System.out.print("Enter the year: ");
        int year = scanner.nextInt();
        scanner.nextLine(); // clear buffer

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

        Collections.sort(transactions);

        for (Transaction transaction : transactions) {
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

        Collections.sort(transactions);

        for (Transaction transaction : transactions) {
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
        Collections.sort(transactions);

        System.out.printf("%-15s %-10s %-30s %-30s %10s%n",
                "Date", "Time", "Description", "Vendor", "Amount");

        System.out.println("----------------------------------------------------------------------------------------------------------");


        for (Transaction transaction : transactions) {
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
        Collections.sort(transactions);


        System.out.printf("%-15s %-10s %-30s %-30s %10s%n",
                "Date", "Time", "Description", "Vendor", "Amount");

        System.out.println("----------------------------------------------------------------------------------------------------------");


        for (Transaction transaction : transactions) {
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
        Collections.sort(transactions);

        System.out.printf("%-15s %-10s %-30s %-30s %10s%n",
                "Date", "Time", "Description", "Vendor", "Amount");

        System.out.println("----------------------------------------------------------------------------------------------------------");



        for (Transaction transaction : transactions) {

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
                deposit = Math.abs(scanner.nextDouble());
                scanner.nextLine();

                invalidInput = true;
            } catch (InputMismatchException e){
                System.out.println("ERROR: Invalid input. Please enter a valid number to be recorded as amount");
                scanner.nextLine();
            }

        }


        System.out.println("Enter the name of source of income: ");
        String vendor = scanner.next();

        scanner.nextLine();
        System.out.println("Enter income description: ");
        String description = scanner.nextLine();

        LocalTime timeOfTranasaction = getCurrentLocalTime();
        LocalDate dateOfTransaction = LocalDate.from(LocalDateTime.now());


        Transaction currentTransaction = new Transaction();
        currentTransaction.setDate(dateOfTransaction);
        currentTransaction.setTime(timeOfTranasaction);
        currentTransaction.setDescription(description);
        currentTransaction.setVendor(vendor);
        currentTransaction.setAmount(deposit);
        transactions.add(currentTransaction);

        addTransactionToFile(currentTransaction);

        System.out.printf("Deposit of $%.2f recorded successfully.%n", deposit);


    }

    private static void runMakePaymentScreen() {
        double payment = 0;
        boolean validInput = false;

        while(!validInput) {
            try {
                System.out.println("Enter the amount to pay: ");
                payment = Math.abs(scanner.nextDouble()) * -1;
                scanner.nextLine();

                validInput = true;

            } catch (InputMismatchException e) {
                System.out.println("ERROR: Invalid input. Please enter a valid number to be recorded as amount");
                scanner.nextLine();
            }
        }


        System.out.println("Enter the name of the vendor: ");
        String vendor = scanner.next();

        scanner.nextLine();
        System.out.println("Enter expense description: ");
        String description = scanner.nextLine();

        LocalTime timeOfTranasaction = getCurrentLocalTime();
        LocalDate dateOfTransaction = LocalDate.from(LocalDateTime.now());


        Transaction currentTransaction = new Transaction();
        currentTransaction.setDate(dateOfTransaction);
        currentTransaction.setTime(timeOfTranasaction);
        currentTransaction.setDescription(description);
        currentTransaction.setVendor(vendor);
        currentTransaction.setAmount(payment);
        transactions.add(currentTransaction);

        addTransactionToFile(currentTransaction);

        System.out.printf("Payment of $%.2f recorded successfully.%n", payment);
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
            System.out.println("ERROR: Unable to write transaction to file.");
            System.out.println("Please make sure 'transactions.csv' exists and is not corrupted.");
            e.printStackTrace();
        }
    }





    }



