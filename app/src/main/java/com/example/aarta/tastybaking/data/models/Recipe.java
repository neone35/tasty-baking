package com.example.aarta.tastybaking.data.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Entity(tableName = "recipes")
public class Recipe {

    @ColumnInfo(name = "id")
    @PrimaryKey
    @SerializedName("id")
    @Expose
    private int id;
    @ColumnInfo(name = "name")
    @SerializedName("name")
    @Expose
    private String name;
    @ColumnInfo(name = "ingredients")
    @SerializedName("ingredients")
    @Expose
    private List<Ingredient> ingredients;
    @ColumnInfo(name = "steps")
    @SerializedName("steps")
    @Expose
    private List<Step> steps;
    @ColumnInfo(name = "servings")
    @SerializedName("servings")
    @Expose
    private int servings;
    @ColumnInfo(name = "image")
    @SerializedName("image")
    @Expose
    private String image;

    // Constructor used by Room to create Recipes
    public Recipe(int id, String name, int servings, String image, List<Ingredient> ingredients, List<Step> steps) {
        this.id = id;
        this.name = name;
        this.servings = servings;
        this.image = image;
        this.ingredients = ingredients;
        this.steps = steps;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public List<Step> getSteps() {
        return steps;
    }

    public int getServings() {
        return servings;
    }

    public String getImage() {
        return image;
    }

}
