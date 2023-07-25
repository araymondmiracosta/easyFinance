package net.araymond.application;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class Account {
    String name;
    double balance;
    ArrayList<Transaction> transactions;

    public Account(String name, double balance) {
        this.name = name;
        this.balance = balance;
        transactions = new ArrayList<Transaction>();
    }

    public void newTransaction(String category, String description, double amount, LocalDate date, LocalTime time) {
        transactions.add(new Transaction(category, description, amount, date, time));
        balance += amount;
    }

    public void removeTransaction(int index) {
        transactions.remove(index);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public ArrayList<Transaction> getTransactions() {
        return transactions;
    }
}
