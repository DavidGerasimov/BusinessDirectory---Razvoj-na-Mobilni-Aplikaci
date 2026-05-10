package com.davidgerasimov.businessdirectory.api;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class SupabaseClient {

    private static final String BASE_URL = "https://czwygjmnxvzjqekekjqs.supabase.co/rest/v1/";
    private static final String API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImN6d3lnam1ueHZ6anFla2VranFzIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzczOTM4MzAsImV4cCI6MjA5Mjk2OTgzMH0.CKsthpV7hCebFaca5GJ0Kj_xKH_iAXfK7gyZJbne1z0";

    private static OkHttpClient httpClient;

    public static OkHttpClient getClient() {
        if (httpClient == null) {
            httpClient = new OkHttpClient();
        }
        return httpClient;
    }

    public static Request.Builder getRequestBuilder(String endpoint) {
        return new Request.Builder()
                .url(BASE_URL + endpoint)
                .header("apikey", API_KEY)
                .header("Authorization", "Bearer " + API_KEY)
                .header("Content-Type", "application/json")
                .header("Prefer", "return=representation");
    }

    public static String getBaseUrl() {
        return BASE_URL;
    }

    public static String getApiKey() {
        return API_KEY;
    }
}