package com.davidgerasimov.businessdirectory;

import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.davidgerasimov.businessdirectory.api.CompanyRepository;
import com.davidgerasimov.businessdirectory.model.Company;

import java.util.ArrayList;
import java.util.List;

public class AddCompanyActivity extends AppCompatActivity {

    private EditText editName, editAddress, editLatitude, editLongitude,
            editEmail, editPhone, editWebsite;
    private CheckBox checkIndustry, checkFun, checkEducation, checkServices;
    private Button btnSave;
    private CompanyRepository repository = new CompanyRepository();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_company);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Add Company");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        editName = findViewById(R.id.editName);
        editAddress = findViewById(R.id.editAddress);
        editLatitude = findViewById(R.id.editLatitude);
        editLongitude = findViewById(R.id.editLongitude);
        editEmail = findViewById(R.id.editEmail);
        editPhone = findViewById(R.id.editPhone);
        editWebsite = findViewById(R.id.editWebsite);
        checkIndustry = findViewById(R.id.checkIndustry);
        checkFun = findViewById(R.id.checkFun);
        checkEducation = findViewById(R.id.checkEducation);
        checkServices = findViewById(R.id.checkServices);
        btnSave = findViewById(R.id.btnSave);

        btnSave.setOnClickListener(v -> saveCompany());
    }

    private void saveCompany() {
        String name = editName.getText().toString().trim();
        String address = editAddress.getText().toString().trim();
        String latStr = editLatitude.getText().toString().trim();
        String lngStr = editLongitude.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String phone = editPhone.getText().toString().trim();
        String website = editWebsite.getText().toString().trim();

        if (name.isEmpty()) {
            editName.setError("Name is required");
            return;
        }

        List<String> selectedCategories = new ArrayList<>();
        if (checkIndustry.isChecked()) selectedCategories.add("Industry");
        if (checkFun.isChecked()) selectedCategories.add("Fun");
        if (checkEducation.isChecked()) selectedCategories.add("Education");
        if (checkServices.isChecked()) selectedCategories.add("Services");

        if (selectedCategories.isEmpty()) {
            Toast.makeText(this, "Please select at least one category", Toast.LENGTH_SHORT).show();
            return;
        }

        double latitude = latStr.isEmpty() ? 0 : Double.parseDouble(latStr);
        double longitude = lngStr.isEmpty() ? 0 : Double.parseDouble(lngStr);

        for (String category : selectedCategories) {
            Company company = new Company();
            company.setName(name);
            company.setAddress(address);
            company.setLatitude(latitude);
            company.setLongitude(longitude);
            company.setEmail(email);
            company.setPhone(phone);
            company.setWebsite(website);
            company.setCategory(category);
            company.setLogoUrl("");

            repository.saveCompany(company, new CompanyRepository.SaveCallback() {
                @Override
                public void onSuccess() {
                    runOnUiThread(() -> {
                        Toast.makeText(AddCompanyActivity.this,
                                "Company saved successfully!", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                }

                @Override
                public void onError(String error) {
                    runOnUiThread(() -> {
                        Toast.makeText(AddCompanyActivity.this,
                                "Error saving company: " + error, Toast.LENGTH_LONG).show();
                    });
                }
            });
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}