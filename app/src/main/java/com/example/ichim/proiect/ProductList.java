package com.example.ichim.proiect;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by ichim on 08-Nov-15.
 */
public class ProductList implements Parcelable{
    private String title;
    private ArrayList<Product> list;
    private double value;
    private float rating=0;

    public ProductList() {
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    protected ProductList(Parcel in) {
        title = in.readString();
        list = in.createTypedArrayList(Product.CREATOR);
        value = in.readDouble();
        rating=in.readFloat();
    }

    public static final Creator<ProductList> CREATOR = new Creator<ProductList>() {
        @Override
        public ProductList createFromParcel(Parcel in) {
            return new ProductList(in);
        }

        @Override
        public ProductList[] newArray(int size) {
            return new ProductList[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<Product> getList() {
        return list;
    }

    public void setList(ArrayList<Product> list) {
        this.list = list;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeTypedList(list);
        dest.writeDouble(value);
        dest.writeFloat(rating);
    }
}
