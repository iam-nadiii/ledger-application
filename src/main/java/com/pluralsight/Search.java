package com.pluralsight;

import java.time.*;

public class Search implements Comparable<Search> {

    private LocalDate searchDate;
    private LocalTime searchTime;

    private String startDate;
    private String endDate;
    private String description;
    private String vendor;
    private String minAmount;
    private String maxAmount;


    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public LocalDate getSearchDate() {
        return searchDate;
    }

    public void setSearchDate(LocalDate searchDate) {
        this.searchDate = searchDate;
    }

    public LocalTime getSearchTime() {
        return searchTime;
    }

    public void setSearchTime(LocalTime searchTime) {
        this.searchTime = searchTime;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
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

    public String getMinAmount() {
        return minAmount;
    }

    public void setMinAmount(String minAmount) {
        this.minAmount = minAmount;
    }

    public String getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(String maxAmount) {
        this.maxAmount = maxAmount;
    }

    @Override
    public String toString() {
        return "Search{" +
                "searchDate=" + searchDate +
                ", searchTime=" + searchTime +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", description='" + description + '\'' +
                ", vendor='" + vendor + '\'' +
                ", minAmount='" + minAmount + '\'' +
                ", maxAmount='" + maxAmount + '\'' +
                '}';
    }

    @Override
    public int compareTo(Search other) {
        int dateCompare = other.searchDate.compareTo(this.searchDate);

        if (dateCompare != 0){
            return dateCompare;
        }

        return other.searchTime.compareTo(this.searchTime);
    }




}
