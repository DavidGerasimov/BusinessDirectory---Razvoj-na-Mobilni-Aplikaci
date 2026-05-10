package com.davidgerasimov.businessdirectory.model;

public class Company {
    private int id;
    private String name;
    private String address;
    private double latitude;
    private double longitude;
    private String email;
    private String phone;
    private String website;
    private String category;
    private String logoUrl;

    public Company() {}

    public Company(int id, String name, String address, double latitude, double longitude,
                   String email, String phone, String website, String category, String logoUrl) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.email = email;
        this.phone = phone;
        this.website = website;
        this.category = category;
        this.logoUrl = logoUrl;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getWebsite() { return website; }
    public String getCategory() { return category; }
    public String getLogoUrl() { return logoUrl; }

    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setAddress(String address) { this.address = address; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setWebsite(String website) { this.website = website; }
    public void setCategory(String category) { this.category = category; }
    public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }
}