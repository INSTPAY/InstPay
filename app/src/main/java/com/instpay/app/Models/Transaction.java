package com.instpay.app.Models;

public class Transaction {
    private String id, from, to, amount;
    private long date;
    private int type;

    public Transaction() {}

    public Transaction(String id, String from, String to, String amount, int type) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.amount = amount;
        this.date = System.currentTimeMillis();
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
