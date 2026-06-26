package com.stockmonitor.model;

import java.time.LocalDateTime;

public class Alert {
    public enum Condition {
        GREATER_THAN(">"),
        LESS_THAN("<");

        private final String symbol;

        Condition(String symbol) {
            this.symbol = symbol;
        }

        public String getSymbol() {
            return symbol;
        }

        @Override
        public String toString() {
            return symbol;
        }
    }

    private String symbol;
    private Condition condition;
    private double targetPrice;
    private boolean triggered;
    private LocalDateTime triggeredTime;

    public Alert(String symbol, Condition condition, double targetPrice) {
        this.symbol = symbol;
        this.condition = condition;
        this.targetPrice = targetPrice;
        this.triggered = false;
    }

    public String getSymbol() {
        return symbol;
    }

    public Condition getCondition() {
        return condition;
    }

    public double getTargetPrice() {
        return targetPrice;
    }

    public boolean isTriggered() {
        return triggered;
    }

    public void setTriggered(boolean triggered) {
        this.triggered = triggered;
        if (triggered) {
            this.triggeredTime = LocalDateTime.now();
        }
    }

    public LocalDateTime getTriggeredTime() {
        return triggeredTime;
    }

    @Override
    public String toString() {
        return symbol + " " + condition.getSymbol() + " " + targetPrice;
    }
}
