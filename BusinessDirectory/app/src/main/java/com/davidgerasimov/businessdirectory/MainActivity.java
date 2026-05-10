package com.davidgerasimov.businessdirectory;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.viewpager.widget.ViewPager;

import com.davidgerasimov.businessdirectory.adapter.TabsPagerAdapter;
import com.davidgerasimov.businessdirectory.api.CompanyRepository;
import com.davidgerasimov.businessdirectory.model.Company;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.material.tabs.TabLayout;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private EditText searchField;
    private TabsPagerAdapter pagerAdapter;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private CompanyRepository repository = new CompanyRepository();
    private Set<Integer> notifiedCompanies = new HashSet<>();

    private static final int LOCATION_PERMISSION_REQUEST = 1001;
    private static final int NOTIFICATION_PERMISSION_REQUEST = 1002;
    private static final String CHANNEL_ID = "proximity_channel";
    private final String[] CATEGORIES = {"Services", "Fun", "Industry", "Education"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        searchField = findViewById(R.id.searchField);

        createNotificationChannel();
        setupTabs();
        setupSearch();
        setupLocation();
        requestNotificationPermission();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Nearby Companies",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Notifies you when you are near a company");
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        NOTIFICATION_PERMISSION_REQUEST);
            }
        }
    }

    private void setupTabs() {
        pagerAdapter = new TabsPagerAdapter(getSupportFragmentManager());
        for (String category : CATEGORIES) {
            pagerAdapter.addFragment(CategoryFragment.newInstance(category), category);
        }
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupSearch() {
        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int currentTab = viewPager.getCurrentItem();
                CategoryFragment fragment = pagerAdapter.getFragment(currentTab);
                if (fragment != null) {
                    fragment.loadCompanies(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                Location userLocation = locationResult.getLastLocation();
                if (userLocation != null) {
                    checkNearbyCompanies(userLocation);
                }
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST);
        } else {
            startLocationUpdates();
        }
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) return;

        LocationRequest locationRequest = new LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY, 10000)
                .setMinUpdateIntervalMillis(5000)
                .build();

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    private void checkNearbyCompanies(Location userLocation) {
        for (String category : CATEGORIES) {
            repository.getCompaniesByCategory(category, null,
                    new CompanyRepository.CompanyCallback() {
                        @Override
                        public void onSuccess(List<Company> companies) {
                            for (Company company : companies) {
                                float[] results = new float[1];
                                Location.distanceBetween(
                                        userLocation.getLatitude(),
                                        userLocation.getLongitude(),
                                        company.getLatitude(),
                                        company.getLongitude(),
                                        results);
                                if (results[0] < 50 && !notifiedCompanies.contains(company.getId())) {
                                    notifiedCompanies.add(company.getId());
                                    sendProximityNotification(company);
                                }
                            }
                        }

                        @Override
                        public void onError(String error) {}
                    });
        }
    }

    private void sendProximityNotification(Company company) {
        Intent intent = new Intent(this, CompanyDetailActivity.class);
        intent.putExtra("name", company.getName());
        intent.putExtra("address", company.getAddress());
        intent.putExtra("phone", company.getPhone());
        intent.putExtra("email", company.getEmail());
        intent.putExtra("website", company.getWebsite());
        intent.putExtra("category", company.getCategory());
        intent.putExtra("logoUrl", company.getLogoUrl());
        intent.putExtra("latitude", company.getLatitude());
        intent.putExtra("longitude", company.getLongitude());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, company.getId(),
                intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_menu_mylocation)
                .setContentTitle("You are near " + company.getName() + "!")
                .setContentText(company.getAddress())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED) {
            NotificationManagerCompat.from(this).notify(company.getId(), builder.build());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        menu.add(0, 1, 0, "Add Company")
                .setIcon(android.R.drawable.ic_menu_add)
                .setShowAsAction(android.view.MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull android.view.MenuItem item) {
        if (item.getItemId() == 1) {
            startActivity(new Intent(this, AddCompanyActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (fusedLocationClient != null && locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }
}