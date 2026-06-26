package com.stockmonitor.service;

import com.stockmonitor.model.Alert;
import com.stockmonitor.model.Stock;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class AlertManager {
    private final List<Alert> activeAlerts;
    private final StockService stockService;
    private final ScheduledExecutorService executorService;
    private Consumer<Alert> onAlertTriggeredCallback;

    public AlertManager(StockService stockService) {
        this.activeAlerts = new CopyOnWriteArrayList<>();
        this.stockService = stockService;
        this.executorService = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "Alert-Checker-Thread");
            t.setDaemon(true);
            return t;
        });
    }

    public void setOnAlertTriggeredCallback(Consumer<Alert> onAlertTriggeredCallback) {
        this.onAlertTriggeredCallback = onAlertTriggeredCallback;
    }

    public void startChecking() {
        // Check alerts every 2 seconds
        executorService.scheduleAtFixedRate(this::checkAlerts, 1, 2, TimeUnit.SECONDS);
    }

    public void stopChecking() {
        executorService.shutdownNow();
    }

    public void addAlert(Alert alert) {
        activeAlerts.add(alert);
    }
    
    public List<Alert> getActiveAlerts() {
    	return activeAlerts;
    }

    private void checkAlerts() {
        for (Alert alert : activeAlerts) {
            // Already triggered alerts shouldn't trigger repeatedly without reset mechanism, 
            // but for simplicity we keep them or we can mark them triggered.
            if (alert.isTriggered()) {
                continue;
            }

            Stock currentStock = stockService.getStock(alert.getSymbol());
            if (currentStock == null || currentStock.getPrice() <= 0) {
                continue; // Data not available yet
            }

            boolean isTriggered = false;
            double currentPrice = currentStock.getPrice();

            if (alert.getCondition() == Alert.Condition.GREATER_THAN && currentPrice > alert.getTargetPrice()) {
                isTriggered = true;
            } else if (alert.getCondition() == Alert.Condition.LESS_THAN && currentPrice < alert.getTargetPrice()) {
                isTriggered = true;
            }

            if (isTriggered) {
                alert.setTriggered(true);
                if (onAlertTriggeredCallback != null) {
                    onAlertTriggeredCallback.accept(alert);
                }
            }
        }
    }
}
