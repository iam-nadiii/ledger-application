package com.pluralsight;

import java.time.LocalDate;
import java.time.LocalTime;


public class Transaction implements Comparable<Transaction> {
    private LocalDate date;
    private LocalTime time;
    private String description;
    private String vendor;
    private double amount;

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    @Override
    public int compareTo(Transaction other) {
        int dateCompare = other.date.compareTo(this.date);

        if (dateCompare != 0){
            return dateCompare;
        }

        return this.time.compareTo(other.time);
    }



    @Override
    public String toString() {
        return "Transaction{" +
                "date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", description='" + description + '\'' +
                ", vendor='" + vendor + '\'' +
                ", amount=" + amount +
                '}';
    }



}


