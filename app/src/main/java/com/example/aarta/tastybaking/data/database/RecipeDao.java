package com.example.aarta.tastybaking.data.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.example.aarta.tastybaking.data.models.Ingredient;
import com.example.aarta.tastybaking.data.models.Recipe;
import com.example.aarta.tastybaking.data.models.Step;

import java.util.List;

@Dao
public interface RecipeDao {
    @Query("SELECT id, name, servings FROM Recipe")
    LiveData<List<Recipe>> getAll();

    @Query("SELECT id, name, servings FROM Recipe where id = :id")
    LiveData<Recipe> getById(int id);

    @Query("SELECT * FROM Ingredient where recipeId = :recipeID")
    LiveData<List<Ingredient>> getIngredients(int recipeID);

    @Query("SELECT * FROM Step where recipeId = :recipeID")
    LiveData<List<Step>> getSteps(int recipeID);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void bulkInsert(Recipe... recipes);

    @Query("DELETE FROM Recipe")
    void deleteAllRecipes();
}
