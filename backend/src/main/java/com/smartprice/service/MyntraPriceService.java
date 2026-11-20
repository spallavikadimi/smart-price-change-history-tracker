package com.smartprice.service;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MyntraPriceService {

    public static JsonNode getProductDetails(String productUrl) throws Exception {

        String apiKey = System.getenv("SCRAPINGDOG_API_KEY");

        if (apiKey == null || apiKey.isEmpty()) {
            throw new Exception("Scrapingdog API key not found.");
        }

        String apiUrl = "https://api.scrapingdog.com/myntra/product"
                + "?api_key=" + URLEncoder.encode(apiKey, StandardCharsets.UTF_8)
                + "&url=" + URLEncoder.encode(productUrl, StandardCharsets.UTF_8);

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .GET()
                .build();

        HttpResponse<String> response = client.send(
                request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new Exception("Unable to fetch Myntra product. API status: "
                    + response.statusCode());
        }

        ObjectMapper mapper = new ObjectMapper();

        return mapper.readTree(response.body())
                .path("product_results");
    }
}