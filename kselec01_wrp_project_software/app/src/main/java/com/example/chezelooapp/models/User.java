package com.example.chezelooapp.models;

public class User {
    private String name;
    private String email;
    private String imageUrl;
    private String UID;

    public User(String name, String email, String imageUrl, String UID) {
        this.name = name;
        this.email = email;
        this.imageUrl = imageUrl;
        this.UID = UID;
    }
    @SuppressWarnings("unused")
    public User() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", UID='" + UID + '\'' +
                '}';
    }
}
