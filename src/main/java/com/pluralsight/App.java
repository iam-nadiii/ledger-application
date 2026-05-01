package com.pluralsight;

import java.io.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.time.*;

import static java.lang.Double.*;


public class App  {

    // Scanner for user input throughout the application
    static Scanner scanner = new Scanner(System.in);

    // Stores all transaction records (loaded from file + user input)
    static ArrayList<Transaction> transactions = new ArrayList<>();

    // Stores all past searches for reuse
    static ArrayList<Search> searches = new ArrayList<>();


    public static void main(String[] args) {

        // Get current date (not used but initialized here)
        LocalDate currentDate = LocalDate.from(LocalDateTime.now());

        // Print current time when application starts
        System.out.println(getCurrentLocalTime());

        // Load transactions from CSV file into memory
        loadTransactionsFromFile("transactions.csv");

        // Load past searches from CSV file into memory
        loadSearchHistoryFromFile("searches.csv");

        // Start main UI
        runHomeScreen();
    }

    private static LocalTime getCurrentLocalTime() {

        // Get current system time
        LocalTime currentTime = LocalTime.from(LocalDateTime.now());

        // Format time into HH:mm:ss
        DateTimeFormatter formattedTime = DateTimeFormatter.ofPattern("HH:mm:ss");
        String formattedTime1 = currentTime.format(formattedTime);

        // Convert formatted string back into LocalTime
        LocalTime fullyFormattedTime = LocalTime.parse(formattedTime1);

        return fullyFormattedTime;
    }

    private static void loadTransactionsFromFile(String fileName) {

        try {
            // Open file for reading
            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String input;

            // Skip header row
            bufferedReader.readLine();

            // Read each line and convert into Transaction object
            while ((input = bufferedReader.readLine()) != null) {

                // Split CSV row into fields
                String[] transactionAttributes = input.split("\\|");

                // Create transaction object and populate fields
                Transaction currentTransaction = new Transaction();
                currentTransaction.setDate(LocalDate.parse(transactionAttributes[0]));
                currentTransaction.setTime(LocalTime.parse(transactionAttributes[1]));
                currentTransaction.setDescription(transactionAttributes[2]);
                currentTransaction.setVendor(transactionAttributes[3]);
                currentTransaction.setAmount(parseDouble(transactionAttributes[4]));

                // Add transaction to list
                transactions.add(currentTransaction);
            }

            // Sort transactions (most recent first based on compareTo)
            Collections.sort(transactions);

            bufferedReader.close();

        } catch (IOException e){
            // Error handling for file issues
            System.out.println("ERROR: Unable to load transaction data from file.");
            System.out.println("Please make sure 'transactions.csv' exists and is not corrupted.");
            e.printStackTrace();
        }
    }

    private static void runHomeScreen() {
        // Load Home Screen into the console

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

        // Run different methods based on the user's choice

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


    // Run the Ledger Menu Screen
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
        // Run different methods based on the user's choice

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

        // Display the reports menu options to the user
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

        // Read the user's report menu choice
        char userChoice = scanner.nextLine().charAt(0);

        // Run the correct report based on the user's choice
        switch (userChoice) {
            case '1':
                // Display transactions from the current month
                displayMonthToDateReport();
                break;
            case '2':
                // Display transactions from a selected previous month
                displayPreviousMonthReport();
                break;
            case '3':
                // Display transactions from the current year
                displayYearToDateReport();
                break;
            case '4':
                // Display transactions from a selected previous year
                displayPreviousYearReport();
                break;
            case '5':
                // Display transactions for a specific vendor
                displayByVendor();
                break;
            case '6':
                // Let the user create a custom search report
                displayCustomReport();
                break;
            case '7':
                // Show saved searches so the user can reuse one
                displayPastSearches();
                break;
            case '0':
                // Return to the ledger menu
                return;
            default:
                // Handle invalid menu input
                System.out.println("Wrong input. Try again.");
        }
    }

    private static void displayPastSearches() {

        // Total width of the table for proper alignment
        int totalWidth = 121;

        // Top/bottom border and row separator
        String border = "═".repeat(totalWidth);
        String separator = "-".repeat(totalWidth);

        // Title and padding calculation to center it
        String title = "📊 PAST SEARCHES";
        int padding = (totalWidth - title.length()) / 2;

        // Print header section with centered title
        System.out.println("\n" + border);
        System.out.println(" ".repeat(padding) + title);
        System.out.println(border);

        // Print column headers (ID in blue)
        System.out.printf("\u001B[34m%-5s\u001B[0m %-14s %-12s %-12s %-12s %-20s %-18s %-10s %-10s%n",
                "ID", "Search-Date", "Search-Time", "Start", "End", "Description", "Vendor", "Min", "Max");

        System.out.println(separator);

        int index = 1;

        // Loop through all saved searches and display them with an index
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

        // Bottom separator before user input
        System.out.println("------------------------------------------------------------------------------------" +
                "-------------------------------------");

        // Prompt user to select a search by ID
        System.out.print("\n👉 Enter the \u001B[35mID\u001B[0m of the search you want to reuse: ");

        // Read user input and convert it to an integer
        int searchID = Integer.parseInt(scanner.nextLine());

        // Convert user-facing ID (1-based) to list index (0-based)
        int searchIndex = searchID - 1;

        // Retrieve the selected Search object
        Search currentSearch = searches.get(searchIndex);

        // Extract saved filter values from the selected search
        String startDate = currentSearch.getStartDate();
        String endDate = currentSearch.getEndDate();
        String description = currentSearch.getDescription();
        String vendor = currentSearch.getVendor();
        String minAmount = currentSearch.getMinAmount();
        String maxAmount = currentSearch.getMaxAmount();

        // Run the report using the selected search filters
        displayFilteredTransactionsReport(startDate, endDate, description, vendor, minAmount, maxAmount);

        // Record this search again (since it was reused)
        recordCurrentSearch(startDate, endDate, maxAmount, minAmount, description, vendor);
    }

    private static void displayCustomReport() {

        // Display title for custom search section
        System.out.println("=== Custom Search === ");

        // Prompt user for start date filter
        System.out.println("Enter start date (yyyy-MM-dd): ");
        String startDate = scanner.nextLine();

        // Prompt user for end date filter
        System.out.println("Enter end date (yyyy-MM-dd): ");
        String endDate = scanner.nextLine();

        // Prompt user for description filter
        System.out.println("Enter transaction description: ");
        String description = scanner.nextLine();

        // Prompt user for vendor filter
        System.out.println("Enter vendor: ");
        String vendor = scanner.nextLine();

        // Prompt user for minimum amount filter
        System.out.println("Enter minimum amount: ");
        String minAmount = scanner.nextLine();

        // Prompt user for maximum amount filter
        System.out.println("Enter maximum amount: ");
        String maxAmount = scanner.nextLine();

        // Validate date range only if both dates are provided and not marked as N/A
        if (!startDate.equals("N/A") && !startDate.isEmpty()
                && !endDate.equals("N/A") && !endDate.isEmpty()) {

            // If start date is after end date, swap them to correct the range
            if (LocalDate.parse(startDate).isAfter(LocalDate.parse(endDate))) {
                String tempDate = startDate;
                startDate = endDate;
                endDate = tempDate;
            }
        }

        // Run the report using the provided filters
        displayFilteredTransactionsReport(startDate, endDate, description, vendor, minAmount, maxAmount);

        // Save this search so it can be reused later
        recordCurrentSearch(startDate, endDate, maxAmount, minAmount, description, vendor);
    }

    private static void recordCurrentSearch(String startDate, String endDate, String maxAmount, String minAmount,
                                            String description, String vendor) {

        // Create a new Search object to store the user's current filters
        Search currentSearch = new Search();

        // Record the exact time the search was made
        currentSearch.setSearchTime(getCurrentLocalTime());

        // Record the date the search was made
        currentSearch.setSearchDate(LocalDate.from(LocalDateTime.now()));

        // Store all filter values entered by the user
        currentSearch.setStartDate(startDate);
        currentSearch.setEndDate(endDate);
        currentSearch.setMaxAmount(maxAmount);
        currentSearch.setMinAmount(minAmount);
        currentSearch.setDescription(description);
        currentSearch.setVendor(vendor);

        // Add the search to the in-memory list of searches
        searches.add(currentSearch);

        // Sort searches so the most recent ones appear first
        Collections.sort(searches);

        // Save this search to the CSV file for persistence
        addSearchToFile(currentSearch);
    }

    private static void addSearchToFile(Search currentSearch) {

        // Retrieve all fields from the Search object
        String searchedStartDate = currentSearch.getStartDate();
        String searchedEndDate = currentSearch.getEndDate();
        String searchedMaxAmount = currentSearch.getMaxAmount();
        String searchedMinAmount = currentSearch.getMinAmount();
        String searchedDescription = currentSearch.getDescription();
        String searchedVendor = currentSearch.getVendor();

        // Replace empty fields with "N/A" before saving to file
        searchedStartDate = searchedStartDate.isEmpty()?  "N/A": searchedStartDate;
        searchedEndDate = searchedEndDate.isEmpty()?  "N/A": searchedEndDate;
        searchedMaxAmount = searchedMaxAmount.isEmpty()?  "N/A": searchedMaxAmount;
        searchedMinAmount = searchedMinAmount.isEmpty()?  "N/A": searchedMinAmount;
        searchedDescription = searchedDescription.isEmpty()?  "N/A": searchedDescription;
        searchedVendor = searchedVendor.isEmpty()?  "N/A": searchedVendor;

        try{
            // Open searches.csv in append mode (true = don't overwrite existing data)
            FileWriter fileWriter = new FileWriter("searches.csv", true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            // Write the search data as a new line in pipe-separated format
            bufferedWriter.write("\n" + currentSearch.getSearchDate() +  "|" + currentSearch.getSearchTime() + "|" +
                    searchedStartDate + "|" + searchedEndDate + "|" + searchedDescription
                    + "|" + searchedVendor + "|" + searchedMinAmount + "|" + searchedMaxAmount);

            // Close the writer to save changes
            bufferedWriter.close();

        } catch (IOException e){
            // Handle file writing errors
            System.out.println("ERROR: Unable to write current search into file.");
            System.out.println("Please make sure 'searches.csv' exists and is not corrupted.");
            e.printStackTrace();
        }
    }


    private static void loadSearchHistoryFromFile(String fileName){
        try {
            // Open the searches file for reading
            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String input;

            // Skip the header row in the CSV file
            bufferedReader.readLine();

            // Read each saved search line from the file
            while ((input = bufferedReader.readLine()) != null) {

                // Split the line into individual fields using pipe delimiter
                String[] searchAttributes = input.split("\\|");

                // Create a Search object and populate it with file data
                Search currentSearch = new Search();
                currentSearch.setSearchDate(LocalDate.parse(searchAttributes[0]));
                currentSearch.setSearchTime(LocalTime.parse(searchAttributes[1]));
                currentSearch.setStartDate(searchAttributes[2]);
                currentSearch.setEndDate(searchAttributes[3]);
                currentSearch.setDescription(searchAttributes[4]);
                currentSearch.setVendor(searchAttributes[5]);
                currentSearch.setMinAmount(searchAttributes[6]);
                currentSearch.setMaxAmount(searchAttributes[7]);

                // Add the search to the in-memory list
                searches.add(currentSearch);
            }

            // Sort searches so the most recent ones appear first
            Collections.sort(searches);

            // Close the file reader
            bufferedReader.close();

        } catch (IOException e){
            // Handle file reading errors
            System.out.println("ERROR: Unable to load search history from file.");
            System.out.println("Please make sure 'searches.csv' exists and is not corrupted.");
            e.printStackTrace();
        }
    }


    private static void displayFilteredTransactionsReport(String startDate, String endDate, String description, String vendor,
                                                          String minAmount, String maxAmount) {

        // Track total income (positive amounts)
        double totalIncome = 0;

        // Track total expenses (negative amounts)
        double totalExpenses = 0;

        // Print report header
        System.out.println("\n=========================================================================" +
                "=================================");
        System.out.println("                                      CUSTOM REPORT");
        System.out.println("===============================================================================" +
                "===========================");

        // Print column headers
        System.out.printf("%-15s %-10s %-30s %-30s %10s%n",
                "Date", "Time", "Description", "Vendor", "Amount");

        System.out.println("---------------------------------------------------------------------------------------" +
                "-------------------");

        // Sort transactions before displaying
        Collections.sort(transactions);

        // Loop through all transactions and apply filters
        for (Transaction transaction : transactions) {

            // Flags to determine if transaction passes each filter
            boolean startDateMatches = true;
            boolean endDateMatches = true;
            boolean descriptionMatches = true;
            boolean vendorMatches = true;
            boolean minAmountMatches = true;
            boolean maxAmountMatches = true;

            // Check if transaction date is before the start date (inclusive filter)
            if (!startDate.equals("N/A") && !startDate.isEmpty() &&
                    transaction.getDate().isBefore(LocalDate.parse(startDate))) {
                startDateMatches = false;
            }

            // Check if transaction date is after the end date (inclusive filter)
            if (!endDate.equals("N/A") && !endDate.isEmpty() &&
                    transaction.getDate().isAfter(LocalDate.parse(endDate))) {
                endDateMatches = false;
            }

            // Check if description matches exactly (case-insensitive)
            if (!description.equals("N/A") && !description.isEmpty() &&
                    !transaction.getDescription().equalsIgnoreCase(description)) {
                descriptionMatches = false;
            }

            // Check if vendor partially matches (case-insensitive)
            if (!vendor.equals("N/A") && !vendor.isEmpty() &&
                    !transaction.getVendor().toLowerCase().contains(vendor.toLowerCase())) {
                vendorMatches = false;
            }

            // Check if transaction amount is below minimum
            if (!minAmount.equals("N/A") && !minAmount.isEmpty() &&
                    transaction.getAmount() < Double.parseDouble(minAmount)) {
                minAmountMatches = false;
            }

            // Check if transaction amount is above maximum
            if (!maxAmount.equals("N/A") && !maxAmount.isEmpty() &&
                    transaction.getAmount() > Double.parseDouble(maxAmount)) {
                maxAmountMatches = false;
            }

            // Only display transaction if it passes ALL filters
            if (startDateMatches && endDateMatches && descriptionMatches &&
                    vendorMatches && minAmountMatches && maxAmountMatches) {

                double amount = transaction.getAmount();

                // Add to totals
                if (amount > 0) totalIncome += amount;
                else totalExpenses += amount;

                // Print transaction row with colored amount
                System.out.printf("%-15s %-10s %-30s %-30s %s$%10.2f\u001B[0m%n",
                        transaction.getDate(),
                        transaction.getTime(),
                        transaction.getDescription(),
                        transaction.getVendor(),
                        colorAmount(amount),
                        amount);
            }
        }

        // Calculate net balance
        double net = totalIncome + totalExpenses;

        // Print totals footer
        System.out.println("------------------------------------------------------------------------------" +
                "----------------------------");
        System.out.printf("%-85s $%10.2f%n", "Total Income:", totalIncome);
        System.out.printf("%-85s $%10.2f%n", "Total Expenses:", totalExpenses);
        System.out.printf("%-85s $%10.2f%n", "Net Balance:", net);
        System.out.println("==============================================================================" +
                "============================\n");
    }



    private static void displayByVendor() {

        // Prompt user to enter a vendor name for filtering
        System.out.print("Enter the vendor you want a report on: ");
        String vendor = scanner.nextLine();

        // Track totals for this vendor
        double totalIncome = 0;
        double totalExpenses = 0;

        // Print report header with vendor name (converted to uppercase for emphasis)
        System.out.println("\n=====================================================================================" +
                "=====================");
        System.out.println("                              VENDOR STATEMENT (" + vendor.toUpperCase() + ")");
        System.out.println("======================================================================================" +
                "====================");

        // Print column headers (consistent spacing with other reports)
        System.out.printf("%-15s %-10s %-30s %-30s %10s%n",
                "Date", "Time", "Description", "Vendor", "Amount");

        System.out.println("---------------------------------------------------------------------------" +
                "-------------------------------");

        // Sort transactions so newest appear first
        Collections.sort(transactions);

        // Loop through all transactions and filter by vendor
        for (Transaction transaction : transactions) {
            if (transaction.getVendor().equalsIgnoreCase(vendor)) {

                double amount = transaction.getAmount();

                // Add to totals depending on whether it's income or expense
                if (amount > 0) totalIncome += amount;
                else totalExpenses += amount;

                // Print matching transaction with colored amount
                System.out.printf("%-15s %-10s %-30s %-30s %s$%10.2f\u001B[0m%n",
                        transaction.getDate(),
                        transaction.getTime(),
                        transaction.getDescription(),
                        transaction.getVendor(),
                        colorAmount(amount),
                        amount);
            }
        }

        // Calculate net total for this vendor
        double net = totalIncome + totalExpenses;

        // Print totals summary
        System.out.println("------------------------------------------------------------------------" +
                "----------------------------------");
        System.out.printf("%-85s $%10.2f%n", "Total Income:", totalIncome);
        System.out.printf("%-85s $%10.2f%n", "Total Expenses:", totalExpenses);
        System.out.printf("%-85s $%10.2f%n", "Net Balance:", net);
        System.out.println("=================================================================" +
                "=========================================\n");
    }



    private static void displayPreviousYearReport() {

        // Prompt user for the year to generate the report
        System.out.print("Enter the year you want a report on: ");
        int year = scanner.nextInt();

        // Clear leftover newline from input buffer
        scanner.nextLine();

        // Track totals for the selected year
        double totalIncome = 0;
        double totalExpenses = 0;

        // Print report header with selected year
        System.out.println("\n=================================================================================" +
                "=========================");
        System.out.println("                                      YEARLY STATEMENT (" + year + ")");
        System.out.println("================================================================================" +
                "==========================");

        // Print column headers
        System.out.printf("%-15s %-10s %-30s %-30s %10s%n",
                "Date", "Time", "Description", "Vendor", "Amount");

        System.out.println("------------------------------------------------------------------------------" +
                "----------------------------");

        // Sort transactions before processing
        Collections.sort(transactions);

        // Loop through transactions and filter by year
        for (Transaction transaction : transactions) {
            if (transaction.getDate().getYear() == year) {

                double amount = transaction.getAmount();

                // Add to income or expenses totals
                if (amount > 0) totalIncome += amount;
                else totalExpenses += amount;

                // Print matching transaction with color formatting
                System.out.printf("%-15s %-10s %-30s %-30s %s$%10.2f\u001B[0m%n",
                        transaction.getDate(),
                        transaction.getTime(),
                        transaction.getDescription(),
                        transaction.getVendor(),
                        colorAmount(amount),
                        amount);
            }
        }

        // Calculate net balance for the year
        double net = totalIncome + totalExpenses;

        // Print totals summary
        System.out.println("-------------------------------------------------------------------------" +
                "---------------------------------");
        System.out.printf("%-85s $%10.2f%n", "Total Income:", totalIncome);
        System.out.printf("%-85s $%10.2f%n", "Total Expenses:", totalExpenses);
        System.out.printf("%-85s $%10.2f%n", "Net Balance:", net);
        System.out.println("=============================================================================" +
                "=============================\n");
    }


    private static void displayYearToDateReport() {

        // Get today's date
        LocalDate currentDate = LocalDate.now();

        // Extract the current year
        int currentYear = currentDate.getYear();

        // Track totals for current year
        double totalIncome = 0;
        double totalExpenses = 0;

        // Print report header with date range (Jan → today)
        System.out.println("\n==============================================================================" +
                "============================");
        System.out.println("                                      YEAR TO DATE STATEMENT");
        System.out.println("                                      January - " + currentDate);
        System.out.println("================================================================================" +
                "==========================");

        // Print column headers (aligned with other reports)
        System.out.printf("%-15s %-10s %-30s %-30s %10s%n",
                "Date", "Time", "Description", "Vendor", "Amount");

        System.out.println("-------------------------------------------------------------------------------" +
                "---------------------------");

        // Sort transactions so newest appear first
        Collections.sort(transactions);

        // Loop through all transactions and filter by current year
        for (Transaction transaction : transactions) {
            if (transaction.getDate().getYear() == currentYear) {

                double amount = transaction.getAmount();

                // Add to income or expense totals
                if (amount > 0) totalIncome += amount;
                else totalExpenses += amount;

                // Print matching transaction with colored amount
                System.out.printf("%-15s %-10s %-30s %-30s %s$%10.2f\u001B[0m%n",
                        transaction.getDate(),
                        transaction.getTime(),
                        transaction.getDescription(),
                        transaction.getVendor(),
                        colorAmount(amount),
                        amount);
            }
        }

        // Calculate net balance (income + expenses)
        double net = totalIncome + totalExpenses;

        // Print totals summary
        System.out.println("------------------------------------------------------------------------" +
                "----------------------------------");
        System.out.printf("%-85s $%10.2f%n", "Total Income:", totalIncome);
        System.out.printf("%-85s $%10.2f%n", "Total Expenses:", totalExpenses);
        System.out.printf("%-85s $%10.2f%n", "Net Balance:", net);
        System.out.println("===========================================================================" +
                "===============================\n");
    }



    private static void displayPreviousMonthReport() {

        // Prompt user for month (1–12)
        System.out.print("Enter the month (1-12): ");
        int month = scanner.nextInt();

        // Prompt user for year
        System.out.print("Enter the year: ");
        int year = scanner.nextInt();

        // Clear leftover newline from input buffer
        scanner.nextLine();

        // Track totals for selected month/year
        double totalIncome = 0;
        double totalExpenses = 0;

        // Print report header with selected month and year
        System.out.println("\n===================================================================================" +
                "=======================");
        System.out.println("                          MONTHLY STATEMENT (" + month + "/" + year + ")");
        System.out.println("======================================================================================" +
                "====================");

        // Print column headers
        System.out.printf("%-15s %-10s %-30s %-30s %10s%n",
                "Date", "Time", "Description", "Vendor", "Amount");

        System.out.println("-------------------------------------------------------------------------------------" +
                "---------------------");

        // Sort transactions before processing
        Collections.sort(transactions);

        // Loop through transactions and filter by selected month and year
        for (Transaction transaction : transactions) {
            if (transaction.getDate().getYear() == year &&
                    transaction.getDate().getMonthValue() == month) {

                double amount = transaction.getAmount();

                // Add to totals based on sign of amount
                if (amount > 0) totalIncome += amount;
                else totalExpenses += amount;

                // Print matching transaction row
                System.out.printf("%-15s %-10s %-30s %-30s %s$%10.2f\u001B[0m%n",
                        transaction.getDate(),
                        transaction.getTime(),
                        transaction.getDescription(),
                        transaction.getVendor(),
                        colorAmount(amount),
                        amount);
            }
        }

        // Calculate net balance for the selected period
        double net = totalIncome + totalExpenses;

        // Print totals summary
        System.out.println("-------------------------------------------------------------------" +
                "---------------------------------------");
        System.out.printf("%-85s $%10.2f%n", "Total Income:", totalIncome);
        System.out.printf("%-85s $%10.2f%n", "Total Expenses:", totalExpenses);
        System.out.printf("%-85s $%10.2f%n", "Net Balance:", net);
        System.out.println("=================================================================================" +
                "=========================\n");
    }



    private static void displayMonthToDateReport() {

        // Get today's date
        LocalDate currentDate = LocalDate.now();

        // Extract current month and year for filtering
        int currentMonth = currentDate.getMonthValue();
        int currentYear  = currentDate.getYear();

        // Track totals for current month
        double totalIncome = 0;
        double totalExpenses = 0;

        // Print report header (month-to-date range)
        System.out.println("\n=====================================================================================" +
                "=====================");
        System.out.println("                          MONTH TO DATE STATEMENT");
        System.out.println("                          " + currentDate.getMonth() + " " + currentYear);
        System.out.println("=======================================================================================" +
                "===================");

        // Print column headers (aligned with other reports)
        System.out.printf("%-15s %-10s %-30s %-30s %10s%n",
                "Date", "Time", "Description", "Vendor", "Amount");

        System.out.println("-------------------------------------------------------------------------------------" +
                "---------------------");

        // Sort transactions so newest appear first
        Collections.sort(transactions);

        // Loop through all transactions and filter by current month and year
        for (Transaction transaction : transactions) {
            if (transaction.getDate().getYear() == currentYear &&
                    transaction.getDate().getMonthValue() == currentMonth) {

                double amount = transaction.getAmount();

                // Add to income or expenses totals
                if (amount > 0) totalIncome += amount;
                else totalExpenses += amount;

                // Print matching transaction with colored amount
                System.out.printf("%-15s %-10s %-30s %-30s %s$%10.2f\u001B[0m%n",
                        transaction.getDate(),
                        transaction.getTime(),
                        transaction.getDescription(),
                        transaction.getVendor(),
                        colorAmount(amount),
                        amount);
            }
        }

        // Calculate net balance for current month
        double net = totalIncome + totalExpenses;

        // Print totals summary
        System.out.println("--------------------------------------------------------------------------------" +
                "--------------------------");
        System.out.printf("%-85s $%10.2f%n", "Total Income:", totalIncome);
        System.out.printf("%-85s $%10.2f%n", "Total Expenses:", totalExpenses);
        System.out.printf("%-85s $%10.2f%n", "Net Balance:", net);
        System.out.println("====================================================================================" +
                "======================\n");
    }


    private static void displayPayments() {

        // Sort transactions before displaying
        Collections.sort(transactions);

        // Report header
        System.out.println("\n======================================================================" +
                "====================================");
        System.out.println("                                      ALL PAYMENTS");
        System.out.println("================================================================================" +
                "==========================");

        // Print column headers
        System.out.printf("%-15s %-10s %-30s %-30s %10s%n",
                "Date", "Time", "Description", "Vendor", "Amount");

        System.out.println("--------------------------------------------------------------------------------------" +
                "--------------------");

        // Loop through transactions and display only payments (negative amounts)
        for (Transaction transaction : transactions) {
            if (transaction.getAmount() < 0) {

                double amount = transaction.getAmount();

                // Print payment transaction with colored amount
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

        // Sort transactions so newest appear first
        Collections.sort(transactions);

        // Report header
        System.out.println("\n======================================================================" +
                "====================================");
        System.out.println("                                      ALL DEPOSITS");
        System.out.println("================================================================================" +
                "==========================");

        // Print column headers
        System.out.printf("%-15s %-10s %-30s %-30s %10s%n",
                "Date", "Time", "Description", "Vendor", "Amount");

        // Print separator line
        System.out.println("-------------------------------------------------------------------------------" +
                "---------------------------");

        // Loop through all transactions
        for (Transaction transaction : transactions) {

            // Only display deposits (positive amounts)
            if (transaction.getAmount() > 0) {

                double amount = transaction.getAmount();

                // Print deposit transaction with colored amount
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

        // Sort transactions before displaying
        Collections.sort(transactions);


        // Report header
        System.out.println("\n======================================================================" +
                "====================================");
        System.out.println("                                      ALL TRANSACTIONS");
        System.out.println("================================================================================" +
                "==========================");



        // Print column headers
        System.out.printf("%-15s %-10s %-30s %-30s %10s%n",
                "Date", "Time", "Description", "Vendor", "Amount");

        // Print separator line
        System.out.println("----------------------------------------------------------------------------------" +
                "------------------------");

        // Loop through all transactions and display everything (no filtering)
        for (Transaction transaction : transactions) {

            double amount = transaction.getAmount();

            // Print transaction with colored amount (green for deposits, red for payments)
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

        // Return green color code for deposits/positive amounts
        if (amount > 0) return "\u001B[32m";

        // Return red color code for payments/negative amounts
        if (amount < 0) return "\u001B[31m";

        // Return default color if amount is zero
        return "\u001B[0m";
    }



    private static void runAddDepositScreen() {

        // Tracks whether the user entered a valid deposit amount
        boolean invalidInput = false;

        // Stores the deposit amount
        double deposit = 0;

        // Keep asking until the user enters a valid number
        while(!invalidInput){
            try {
                System.out.println("Enter the amount to deposit: ");

                // Convert deposit to positive number
                deposit = Math.abs(scanner.nextDouble());

                // Clear leftover newline from scanner
                scanner.nextLine();

                // Stop loop after valid input
                invalidInput = true;

            } catch (InputMismatchException e){

                // Handle invalid non-number input
                System.out.println("ERROR: Invalid input. Please enter a valid number to be recorded as amount");
                scanner.nextLine();
            }
        }

        // Ask user for income source/vendor
        System.out.println("Enter the name of source of income: ");
        String vendor = scanner.nextLine();


        // Ask user for income description
        System.out.println("Enter income description: ");
        String description = scanner.nextLine();

        // Get current time and date for this transaction
        LocalTime timeOfTranasaction = getCurrentLocalTime();
        LocalDate dateOfTransaction = LocalDate.from(LocalDateTime.now());

        // Create and fill a new Transaction object
        Transaction currentTransaction = new Transaction();
        currentTransaction.setDate(dateOfTransaction);
        currentTransaction.setTime(timeOfTranasaction);
        currentTransaction.setDescription(description);
        currentTransaction.setVendor(vendor);
        currentTransaction.setAmount(deposit);

        // Add transaction to list
        transactions.add(currentTransaction);

        // Save transaction to file
        addTransactionToFile(currentTransaction);

        // Confirm successful deposit
        System.out.printf("Deposit of $%.2f recorded successfully.%n", deposit);
    }



    private static void runMakePaymentScreen() {

        // Stores the payment amount
        double payment = 0;

        // Tracks whether the user entered a valid payment amount
        boolean validInput = false;

        // Keep asking until the user enters a valid number
        while(!validInput) {
            try {
                System.out.println("Enter the amount to pay: ");

                // Convert payment to a negative number
                payment = Math.abs(scanner.nextDouble()) * -1;

                // Clear leftover newline from scanner
                scanner.nextLine();

                // Stop loop after valid input
                validInput = true;

            } catch (InputMismatchException e) {

                // Handle invalid non-number input
                System.out.println("ERROR: Invalid input. Please enter a valid number to be recorded as amount");
                scanner.nextLine();
            }
        }

        // Ask user for payment vendor
        System.out.println("Enter the name of the vendor: ");
        String vendor = scanner.nextLine();


        // Ask user for expense description
        System.out.println("Enter expense description: ");
        String description = scanner.nextLine();

        // Get current time and date for this transaction
        LocalTime timeOfTranasaction = getCurrentLocalTime();
        LocalDate dateOfTransaction = LocalDate.from(LocalDateTime.now());

        // Create and fill a new Transaction object
        Transaction currentTransaction = new Transaction();
        currentTransaction.setDate(dateOfTransaction);
        currentTransaction.setTime(timeOfTranasaction);
        currentTransaction.setDescription(description);
        currentTransaction.setVendor(vendor);
        currentTransaction.setAmount(payment);

        // Add transaction to list
        transactions.add(currentTransaction);

        // Save transaction to file
        addTransactionToFile(currentTransaction);

        // Confirm successful payment
        System.out.printf("Payment of $%.2f recorded successfully.%n", payment);
    }



    private static void addTransactionToFile(Transaction currentTransaction) {
        try {
            // Open transactions.csv in append mode
            FileWriter fileWriter = new FileWriter("transactions.csv", true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            // Write transaction as a pipe-separated line
            bufferedWriter.write(currentTransaction.getDate() + "|" + currentTransaction.getTime() + "|" +
                    currentTransaction.getDescription() + "|" + currentTransaction.getVendor() + "|" +
                    currentTransaction.getAmount() + "\n");

            // Close writer to save changes
            bufferedWriter.close();

        } catch (IOException e){

            // Handle file writing errors
            System.out.println("ERROR: Unable to write transaction to file.");
            System.out.println("Please make sure 'transactions.csv' exists and is not corrupted.");
            e.printStackTrace();
        }
    }




    }



