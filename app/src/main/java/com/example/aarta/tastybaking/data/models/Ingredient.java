package com.example.aarta.tastybaking.data.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Entity(foreignKeys = @ForeignKey(entity = Recipe.class, parentColumns = "id", childColumns = "recipeId"),
        indices = {@Index(value = "recipeId")})
public class Ingredient {

    @ColumnInfo
    @SerializedName("quantity")
    @Expose
    private float quantity;
    @ColumnInfo
    @SerializedName("measure")
    @Expose
    private String measure;
    @ColumnInfo
    @SerializedName("ingredient")
    @Expose
    private String ingredient;
    @ColumnInfo
    @PrimaryKey
    private int recipeId;

    // Constructor used by Room to create Ingredients
    public Ingredient(float quantity, String measure, String ingredient, int recipeId) {
        this.quantity = quantity;
        this.measure = measure;
        this.ingredient = ingredient;
        this.recipeId = recipeId;
    }

    public float getQuantity() {
        return quantity;
    }

    public String getMeasure() {
        return measure;
    }

    public String getIngredient() {
        return ingredient;
    }

    public int getRecipeId() {
        return recipeId;
    }

}
