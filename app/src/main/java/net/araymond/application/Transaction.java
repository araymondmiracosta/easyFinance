package net.araymond.application;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

public class Transaction implements Serializable {
    String category, description;
    double amount;
    LocalDate date;
    LocalTime time;

    public Transaction(String category, String description, double amount, LocalDate date, LocalTime time) {
        this.category = category;
        this.description = description;
        this.amount = amount;
        this.date = date;
        this.time = time;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

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

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public double getAmount() {
        return amount;
    }
}
