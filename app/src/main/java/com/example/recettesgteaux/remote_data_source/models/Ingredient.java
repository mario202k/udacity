package com.example.recettesgteaux.remote_data_source.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Ingredient implements Parcelable {
    @SerializedName("quantity")
    private double mQuantity;
    @SerializedName("measure")
    private String mMeasure;
    @SerializedName("ingredient")
    private String mName;

    public Ingredient() {
    }

    public double getQuantity() {
        return mQuantity;
    }

    public void setQuantity(double quantity) {
        mQuantity = quantity;
    }

    public String getMeasure() {
        return mMeasure;
    }

    public void setMeasure(String measure) {
        mMeasure = measure;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.mQuantity);
        dest.writeString(this.mMeasure);
        dest.writeString(this.mName);
    }

    protected Ingredient(Parcel in) {
        this.mQuantity = in.readDouble();
        this.mMeasure = in.readString();
        this.mName = in.readString();
    }

    public static final Parcelable.Creator<Ingredient> CREATOR = new Parcelable.Creator<Ingredient>() {
        @Override
        public Ingredient createFromParcel(Parcel source) {
            return new Ingredient(source);
        }

        @Override
        public Ingredient[] newArray(int size) {
            return new Ingredient[size];
        }
    };
}
