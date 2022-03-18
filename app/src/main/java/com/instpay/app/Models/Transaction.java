package com.instpay.app.Models;

import static com.instpay.app.App.ME;

public class Transaction {
    private String _id, from, to, createdAt;
    private double amount;

    public Transaction() {}

    public Transaction(String _id, String to, double amount) {
        this._id = _id;
        this.from = ME.getAccount();
        this.to = to;
        this.amount = amount;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
