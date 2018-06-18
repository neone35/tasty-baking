package com.example.aarta.tastybaking.data.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Entity(foreignKeys = @ForeignKey(entity = Recipe.class, parentColumns = "id", childColumns = "recipeId"),
        indices = {@Index(value = "recipeId")})
public class Step {

    @ColumnInfo
    @SerializedName("id")
    @Expose
    private int id;
    @ColumnInfo
    @SerializedName("shortDescription")
    @Expose
    private String shortDescription;
    @ColumnInfo
    @SerializedName("description")
    @Expose
    private String description;
    @ColumnInfo
    @SerializedName("videoURL")
    @Expose
    private String videoURL;
    @ColumnInfo
    @SerializedName("thumbnailURL")
    @Expose
    private String thumbnailURL;
    @ColumnInfo
    @PrimaryKey
    private int recipeId;

    // Constructor used by Room to create Steps
    public Step(int id, String shortDescription, String description, String videoURL, String thumbnailURL, int recipeId) {
        this.id = id;
        this.shortDescription = shortDescription;
        this.description = description;
        this.videoURL = videoURL;
        this.thumbnailURL = thumbnailURL;
        this.recipeId = recipeId;
    }

    public int getId() {
        return id;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public String getDescription() {
        return description;
    }

    public String getVideoURL() {
        return videoURL;
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }

    public int getRecipeId() {
        return recipeId;
    }

}
