package com.example.chezelooapp.models;

import android.os.Parcel;
import android.os.Parcelable;

public class  Product implements Parcelable, Comparable<Product> {

    private String title;
    private String description;
    private String type;
    private String country;
    private String pairs;
    private String image;
    private String key = null;

    @SuppressWarnings("unused")
    public Product() {
    }

    public Product(String title, String description, String type, String country, String pairs, String image) {
        this.title = title;
        this.description = description;
        this.type = type;
        this.country = country;
        this.pairs = pairs;
        this.image = image;
    }

    private Product(Parcel in) {
        title = in.readString();
        description = in.readString();
        type = in.readString();
        country = in.readString();
        pairs = in.readString();
        image = in.readString();
        key = in.readString();
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getType() {
        return type;
    }

    public String getCountry() {
        return country;
    }

    public String getPairs() {
        return pairs;
    }

    public String getImage() {
        return image;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(type);
        dest.writeString(country);
        dest.writeString(pairs);
        dest.writeString(image);
        dest.writeString(key);
    }


    @Override
    public int compareTo(Product o) {
        return  this.getTitle().compareToIgnoreCase(o.getTitle());
    }
}
