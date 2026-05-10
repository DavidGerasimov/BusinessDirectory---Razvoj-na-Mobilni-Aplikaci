package com.davidgerasimov.businessdirectory.api;

import android.util.Log;

import com.davidgerasimov.businessdirectory.model.Company;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CompanyRepository {

    private static final String TAG = "CompanyRepository";
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    public interface CompanyCallback {
        void onSuccess(List<Company> companies);
        void onError(String error);
    }

    public interface SaveCallback {
        void onSuccess();
        void onError(String error);
    }

    public void getCompaniesByCategory(String category, String search, CompanyCallback callback) {
        String endpoint = "companies?category=eq." + category;
        if (search != null && !search.isEmpty()) {
            endpoint += "&name=ilike.*" + search + "*";
        }
        endpoint += "&select=*";

        Request request = SupabaseClient.getRequestBuilder(endpoint)
                .get()
                .build();

        SupabaseClient.getClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, java.io.IOException e) {
                callback.onError(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws java.io.IOException {
                if (response.isSuccessful()) {
                    try {
                        String body = response.body().string();
                        JSONArray array = new JSONArray(body);
                        List<Company> companies = new ArrayList<>();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            Company c = new Company();
                            c.setId(obj.getInt("id"));
                            c.setName(obj.getString("name"));
                            c.setAddress(obj.optString("address", ""));
                            c.setLatitude(obj.optDouble("latitude", 0));
                            c.setLongitude(obj.optDouble("longitude", 0));
                            c.setEmail(obj.optString("email", ""));
                            c.setPhone(obj.optString("phone", ""));
                            c.setWebsite(obj.optString("website", ""));
                            c.setCategory(obj.optString("category", ""));
                            c.setLogoUrl(obj.optString("logo_url", ""));
                            companies.add(c);
                        }
                        callback.onSuccess(companies);
                    } catch (Exception e) {
                        callback.onError(e.getMessage());
                    }
                } else {
                    callback.onError("Error: " + response.code());
                }
            }
        });
    }

    public void saveCompany(Company company, SaveCallback callback) {
        try {
            JSONObject json = new JSONObject();
            json.put("name", company.getName());
            json.put("address", company.getAddress());
            json.put("latitude", company.getLatitude());
            json.put("longitude", company.getLongitude());
            json.put("email", company.getEmail());
            json.put("phone", company.getPhone());
            json.put("website", company.getWebsite());
            json.put("category", company.getCategory());
            json.put("logo_url", company.getLogoUrl());

            RequestBody body = RequestBody.create(json.toString(), JSON);
            Request request = SupabaseClient.getRequestBuilder("companies")
                    .post(body)
                    .build();

            SupabaseClient.getClient().newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, java.io.IOException e) {
                    callback.onError(e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws java.io.IOException {
                    if (response.isSuccessful()) {
                        callback.onSuccess();
                    } else {
                        callback.onError("Error: " + response.code() + " " + response.body().string());
                    }
                }
            });
        } catch (Exception e) {
            callback.onError(e.getMessage());
        }
    }
}