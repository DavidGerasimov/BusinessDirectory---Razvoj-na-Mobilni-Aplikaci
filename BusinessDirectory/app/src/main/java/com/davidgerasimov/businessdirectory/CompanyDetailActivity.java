package com.davidgerasimov.businessdirectory;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.davidgerasimov.businessdirectory.model.Company;

public class CompanyDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_detail);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Get company data from intent
        String name = getIntent().getStringExtra("name");
        String address = getIntent().getStringExtra("address");
        String phone = getIntent().getStringExtra("phone");
        String email = getIntent().getStringExtra("email");
        String website = getIntent().getStringExtra("website");
        String category = getIntent().getStringExtra("category");
        String logoUrl = getIntent().getStringExtra("logoUrl");
        double latitude = getIntent().getDoubleExtra("latitude", 0);
        double longitude = getIntent().getDoubleExtra("longitude", 0);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(name);
        }

        ImageView logo = findViewById(R.id.detailLogo);
        TextView detailName = findViewById(R.id.detailName);
        TextView detailAddress = findViewById(R.id.detailAddress);
        TextView detailPhone = findViewById(R.id.detailPhone);
        TextView detailEmail = findViewById(R.id.detailEmail);
        TextView detailWebsite = findViewById(R.id.detailWebsite);
        TextView detailCategory = findViewById(R.id.detailCategory);
        Button btnOpenMap = findViewById(R.id.btnOpenMap);

        detailName.setText(name);
        detailAddress.setText(address);
        detailPhone.setText(phone);
        detailEmail.setText(email);
        detailWebsite.setText(website);
        detailCategory.setText(category);

        if (logoUrl != null && !logoUrl.isEmpty()) {
            Glide.with(this).load(logoUrl).into(logo);
        }

        btnOpenMap.setOnClickListener(v -> {
            Uri mapUri = Uri.parse("geo:" + latitude + "," + longitude + "?q=" + Uri.encode(name));
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, mapUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            if (mapIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(mapIntent);
            } else {
                // fallback to browser maps
                Uri browserUri = Uri.parse("https://maps.google.com/?q=" + latitude + "," + longitude);
                startActivity(new Intent(Intent.ACTION_VIEW, browserUri));
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}