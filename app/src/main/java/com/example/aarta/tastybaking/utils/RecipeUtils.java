package com.example.aarta.tastybaking.utils;

import com.example.aarta.tastybaking.data.models.Ingredient;
import com.example.aarta.tastybaking.data.models.Recipe;

import java.util.List;

public final class RecipeUtils {

    public static Recipe[] getRecipeArray(List<Recipe> recipeList) {
        Recipe[] recipeArray = new Recipe[recipeList.size()];
        recipeList.toArray(recipeArray); // fill the array
        return recipeArray;
    }

//    public static Ingredient[] getIngredientArray (Recipe[] recipeArray) {
//        Recipe[] recipeArray = new Recipe[recipeList.size()];
//        recipeList.toArray(recipeArray); // fill the array
//        return recipeArray;
//    }
}
