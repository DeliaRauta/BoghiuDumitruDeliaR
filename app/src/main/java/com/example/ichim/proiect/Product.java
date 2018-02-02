package com.example.ichim.proiect;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ichim on 08-Nov-15.
 */
public class Product implements Parcelable {
    private String name;
    private Category category;
    private double price;

    public Product() {
    }

    public Product(String nume, Category category, double pret) {
        this.name = nume;
        this.category = category;
        this.price = pret;
    }

    protected Product(Parcel in) {
        name = in.readString();
        category=Category.valueOf(in.readString());
        price = in.readDouble();
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

    public String getName() {
        return name;
    }

    public void setName(String nume) {
        this.name = nume;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double pret) {
        this.price = pret;
    }

    @Override
    public String toString() {
        return "Product{" +
                "name='" + name + '\'' +
                ", category=" + category +
                ", price=" + price +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(category.toString());
        dest.writeDouble(price);
    }
}

