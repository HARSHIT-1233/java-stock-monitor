package com.stockmonitor.model;

import java.time.LocalDateTime;

public class Stock {
    private String symbol;
    private double price;
    private double previousPrice;
    private LocalDateTime timestamp;

    public Stock(String symbol, double price, LocalDateTime timestamp) {
        this.symbol = symbol;
        this.price = price;
        this.previousPrice = price;
        this.timestamp = timestamp;
    }

    public String getSymbol() {
        return symbol;
    }

    public double getPrice() {
        return price;
    }

    public double getPreviousPrice() {
        return previousPrice;
    }

    public void setPrice(double price) {
        if (this.price != price) {
            this.previousPrice = this.price;
        }
        this.price = price;
        this.timestamp = LocalDateTime.now();
    }

    public double getPriceChange() {
        if (previousPrice == 0.0) return 0.0;
        return price - previousPrice;
    }

    public double getChangePercent() {
        if (previousPrice == 0.0) return 0.0;
        return ((price - previousPrice) / previousPrice) * 100;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
