package com.example.aarta.tastybaking.data.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.database.Cursor;

import com.example.aarta.tastybaking.data.models.Recipe;

import java.util.List;

@Dao
public interface RecipeDao {
    @Query("SELECT * FROM Recipes")
    LiveData<List<Recipe>> getAll();

    @Query("SELECT * FROM Recipes where id = :id")
    LiveData<Recipe> getById(int id);

    @Query("SELECT * FROM Recipes where id = :recipeID")
    Cursor getStaticRecipeCursor(int recipeID);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void bulkInsert(Recipe... recipes);

    @Query("DELETE FROM Recipes")
    void deleteAllRecipes();
}
