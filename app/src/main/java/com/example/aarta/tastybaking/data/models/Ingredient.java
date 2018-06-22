package com.example.aarta.tastybaking.data.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

// Used by gson to parse response (NetworkUtils) && display in UI (room TypeConverter)
public class Ingredient {

    @SerializedName("quantity")
    @Expose
    private float quantity;
    @SerializedName("measure")
    @Expose
    private String measure;
    @SerializedName("ingredient")
    @Expose
    private String ingredient;

    public float getQuantity() {
        return quantity;
    }

    public String getMeasure() {
        return measure;
    }

    public String getIngredient() {
        return ingredient;
    }

}
