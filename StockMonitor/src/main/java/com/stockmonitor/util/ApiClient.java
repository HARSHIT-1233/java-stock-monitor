package com.stockmonitor.util;

import org.json.JSONArray;
import org.json.JSONObject;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class ApiClient {
    private static final String YAHOO_FINANCE_URL = "https://query1.finance.yahoo.com/v8/finance/chart/";
    private final HttpClient httpClient;

    public ApiClient() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    public double fetchStockPrice(String symbol) throws Exception {
        String url = YAHOO_FINANCE_URL + symbol;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .header("User-Agent", "Mozilla/5.0")
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            JSONObject json = new JSONObject(response.body());
            return parsePriceFromYahooResponse(json);
        } else {
            throw new Exception("API Request failed with status code " + response.statusCode());
        }
    }

    private double parsePriceFromYahooResponse(JSONObject json) throws Exception {
        try {
            JSONObject chart = json.getJSONObject("chart");
            JSONArray result = chart.getJSONArray("result");
            if (result.length() > 0) {
                JSONObject firstResult = result.getJSONObject(0);
                JSONObject meta = firstResult.getJSONObject("meta");
                return meta.getDouble("regularMarketPrice");
            }
        } catch (Exception e) {
            throw new Exception("Failed to parse API response", e);
        }
        throw new Exception("Invalid API response format");
    }
}
