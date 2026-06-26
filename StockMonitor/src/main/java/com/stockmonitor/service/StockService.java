package com.stockmonitor.service;

import com.stockmonitor.model.Stock;
import com.stockmonitor.util.ApiClient;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class StockService {
    private final Map<String, Stock> trackedStocks;
    private final ApiClient apiClient;
    private final ScheduledExecutorService executorService;
    private Consumer<Stock> onUpdateCallback;
    private Consumer<String> onErrorCallback;

    public StockService() {
        this.trackedStocks = new ConcurrentHashMap<>();
        this.apiClient = new ApiClient();
        this.executorService = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "Stock-Fetcher-Thread");
            t.setDaemon(true);
            return t;
        });
    }

    public void setOnUpdateCallback(Consumer<Stock> onUpdateCallback) {
        this.onUpdateCallback = onUpdateCallback;
    }

    public void setOnErrorCallback(Consumer<String> onErrorCallback) {
        this.onErrorCallback = onErrorCallback;
    }

    public void startPolling() {
        // Poll every 5 seconds
        executorService.scheduleAtFixedRate(this::fetchUpdates, 0, 5, TimeUnit.SECONDS);
    }

    public void stopPolling() {
        executorService.shutdownNow();
    }

    public void addStock(String symbol) {
        String upperSymbol = symbol.toUpperCase();
        if (!trackedStocks.containsKey(upperSymbol)) {
            // Add with a default price initially
            trackedStocks.put(upperSymbol, new Stock(upperSymbol, 0.0, LocalDateTime.now()));
            // Fetch immediately
            fetchSingle(upperSymbol);
        }
    }

    public void removeStock(String symbol) {
        trackedStocks.remove(symbol.toUpperCase());
    }

    public Collection<Stock> getAllStocks() {
        return trackedStocks.values();
    }

    public Stock getStock(String symbol) {
        return trackedStocks.get(symbol.toUpperCase());
    }

    private void fetchSingle(String symbol) {
        try {
            double price = apiClient.fetchStockPrice(symbol);
            Stock stock = trackedStocks.get(symbol);
            if (stock != null) {
                stock.setPrice(price);
                if (onUpdateCallback != null) {
                    onUpdateCallback.accept(stock);
                }
            }
        } catch (Exception e) {
            System.err.println("Error fetching " + symbol + ": " + e.getMessage());
            if (onErrorCallback != null) {
                onErrorCallback.accept("Error fetching " + symbol + ": " + e.getMessage());
            }
        }
    }

    private void fetchUpdates() {
        for (String symbol : trackedStocks.keySet()) {
            fetchSingle(symbol);
            // Slight delay to avoid hitting rate limits instantly if there are many stocks
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
