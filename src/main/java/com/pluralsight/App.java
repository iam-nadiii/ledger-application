package com.pluralsight;

import java.util.Scanner;




public class App {
    static Scanner input = new Scanner(System.in);

    public static void main(String[] args) {
        boolean systemIsRunning = true;


        booksList[0] = new Book("The Great Gatsby", "9780743273565", 1, true, "Conor");
        booksList[1] = new Book("To Kill a Mockingbird", "9780061120084", 2, false);
        booksList[2] = new Book("1984", "9780451524935", 3, false);
        booksList[3] = new Book("Pride and Prejudice", "9781503290563", 4, true, "Henry");
        booksList[4] = new Book("The Catcher in the Rye", "9780316769488", 5, false);
        booksList[5] = new Book("The Hobbit", "9780547928227", 6, false);
        booksList[6] = new Book("Moby Dick", "9781503280786", 7, false);
        booksList[7] = new Book("War and Peace", "9780199232765", 8, false);
        booksList[8] = new Book("The Odyssey", "9780140268867", 9, true, "Wilson");
        booksList[9] = new Book("Crime and Punishment", "9780486415871", 10, false);
        booksList[10] = new Book("Brave New World", "9780060850524", 11, false);
        booksList[11] = new Book("Jane Eyre", "9780141441146", 12, true, "Ezra");
        booksList[12] = new Book("The Lord of the Rings", "9780618640157", 13, false);
        booksList[13] = new Book("The Alchemist", "9780061122415", 14, false, "");
        booksList[14] = new Book("Sapiens", "9780062316097", 15, true, "David");
        booksList[15] = new Book("The Kite Runner", "9781594631931", 16, false, "");
        booksList[16] = new Book("The Da Vinci Code", "9780307474278", 17, true, "Sarah");
        booksList[17] = new Book("Harry Potter and the Sorcerer's Stone", "9780590353427", 18, false, "");
        booksList[18] = new Book("The Hunger Games", "9780439023481", 19, false, "");
        booksList[19] = new Book("The Book Thief", "9780375842207", 20, true, "Emily");



        runMyApp();


    }

    private static void runMyApp() {
        boolean systemIsRunning = true;


        do {
            System.out.println("=== Menu ===");
            System.out.println("(1) Show Available Books\n" +
                    "(2) Show Checked Out Books\n" +
                    "(3) Exit - closes out of the application");
            int userChoice = input.nextInt();
            input.nextLine();

            switch (userChoice) {
                case 1:
                    showAvailableBooks(booksList);
                    break;
                case 2:
                    showCheckedOutBooks(booksList);
                    break;
                case 3:
                    System.out.println("Thanks for using our services!");
                    System.out.println("System exiting now.");
                    systemIsRunning = false;
                    return;
                default:
                    System.out.println("Wrong input. Try again.");
            }
        } while (systemIsRunning);
    }

    public static void showAvailableBooks(Book[] books) {
        for (Book book : books) {
            if (book.getIsCheckedOut() == false) {
                System.out.println("[Title: " + book.getTitle() + ", ISBN: " + book.getIsbn() + ", " +
                        "ID: " + book.getId() + "]");
            }
        }
        System.out.println();
        System.out.println("== Menu ==\n" +
                "(1) Check book out\n" +
                "(2) Go back to previous menu");
        int userChoice = input.nextInt();
        input.nextLine();

        switch (userChoice) {
            case 1:
                System.out.println("Enter the title of the book you want to check out: ");
                String title = input.nextLine();

                System.out.println("Enter your name: ");
                String name = input.nextLine();

                for (int i = 0; i < booksList.length; i++) {
                    if (booksList[i].getTitle().equals(title)) {
                        booksList[i].setCheckedOutTo(name);
                        booksList[i].setIsCheckedOut(true);
                    }
                }

                break;
            case 2:
                runMyApp();
                break;

        }
    }

    public static void showCheckedOutBooks (Book[] books){
        System.out.println("=== Checked Out Books ===");
        for (Book book : books) {
            if (book.getIsCheckedOut()  ) {
                System.out.println("[Title: " + book.getTitle() + ", ISBN: " + book.getIsbn() + ", " +
                        "ID: " + book.getId() + ", Checked Out To: " + book.getCheckedOutTo() + "]");
            }

        }

        System.out.println("=== Menu ===\n" +
                "(1) Check In a Book\n" +
                "(2) Go back to home screen");
        int userChoice = input.nextInt();

        switch (userChoice){
            case 1:
                System.out.println("Enter the ID of the book you want to check in: ");
                int id = input.nextInt();

                checkIn(id);
        }

    }

    public static void checkIn(int id){
        for(Book book: booksList){
            if(book.getId() == id){
                book.setIsCheckedOut(false);
                book.setCheckedOutTo("");
            }
        }
    }
}

