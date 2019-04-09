package com.example.recettesgteaux.remote_data_source.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Recipe implements Parcelable {
    @SerializedName("name")
    private String mName;
    @SerializedName("id")
    private int mId;
    @SerializedName("steps")
    private List<Steps> mStepsList;
    @SerializedName("ingredients")
    private List<Ingredient> mIngredientList;
    @SerializedName("servings")
    private int mServings;
    @SerializedName("image")
    private String mImage;

    public Recipe() {
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public List<Steps> getStepsList() {
        return mStepsList;
    }

    public void setStepsList(List<Steps> stepsList) {
        mStepsList = stepsList;
    }

    public List<Ingredient> getIngredientList() {
        return mIngredientList;
    }

    public void setIngredientList(List<Ingredient> ingredientList) {
        mIngredientList = ingredientList;
    }

    public int getServings() {
        return mServings;
    }

    public void setServings(int servings) {
        mServings = servings;
    }

    public String getImage() {
        return mImage;
    }

    public void setImage(String image) {
        mImage = image;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mName);
        dest.writeInt(this.mId);
        dest.writeTypedList(this.mStepsList);
        dest.writeTypedList(this.mIngredientList);
        dest.writeInt(this.mServings);
        dest.writeString(this.mImage);
    }

    protected Recipe(Parcel in) {
        this.mName = in.readString();
        this.mId = in.readInt();
        this.mStepsList = in.createTypedArrayList(Steps.CREATOR);
        this.mIngredientList = in.createTypedArrayList(Ingredient.CREATOR);
        this.mServings = in.readInt();
        this.mImage = in.readString();
    }

    public static final Parcelable.Creator<Recipe> CREATOR = new Parcelable.Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel source) {
            return new Recipe(source);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };
}
