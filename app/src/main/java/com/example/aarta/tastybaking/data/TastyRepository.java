package com.example.aarta.tastybaking.data;

import android.arch.lifecycle.LiveData;

import com.example.aarta.tastybaking.AppExecutors;
import com.example.aarta.tastybaking.utils.RecipeUtils;
import com.example.aarta.tastybaking.data.database.RecipeDao;
import com.example.aarta.tastybaking.data.models.Ingredient;
import com.example.aarta.tastybaking.data.models.Recipe;
import com.example.aarta.tastybaking.data.models.Step;
import com.example.aarta.tastybaking.data.network.RecipesNetworkRoot;
import com.orhanobut.logger.Logger;

import java.util.List;

public class TastyRepository {

    // For Singleton instantiation
    private static final Object LOCK = new Object();
    private static TastyRepository sInstance;

    private final RecipeDao mRecipeDao;
    private final RecipesNetworkRoot mRecipesNetworkRoot;
    private final AppExecutors mExecutors;
    private boolean mInitialized = false;

    private TastyRepository(RecipeDao recipeDao,
                            RecipesNetworkRoot recipesNetworkRoot,
                            AppExecutors executors) {
        mRecipeDao = recipeDao;
        mRecipesNetworkRoot = recipesNetworkRoot;
        mExecutors = executors;

        // If repository exists, observe LiveData and update database on change
        LiveData<List<Recipe>> networkData = mRecipesNetworkRoot.getCurrentRecipes();
        networkData.observeForever(newRecipesFromNetwork -> mExecutors.diskIO().execute(() -> {
            // Delete old recipes if present
            if (mRecipeDao.getAll() != null) {
                mRecipeDao.deleteAllRecipes();
                Logger.d("Old recipes deleted");
            }
            // Insert new recipes into RecipeDatabase
            Recipe[] recipeArray;
            if (newRecipesFromNetwork != null) {
                recipeArray = RecipeUtils.getRecipeArray(newRecipesFromNetwork);
                mRecipeDao.bulkInsert(recipeArray);
                Logger.d("New values inserted");
            } else {
                Logger.d("No response from network");
            }
        }));
    }

    public synchronized static TastyRepository getInstance(
            RecipeDao recipeDao, RecipesNetworkRoot recipesNetworkRoot,
            AppExecutors executors) {
        Logger.d("Getting the repository");
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new TastyRepository(recipeDao, recipesNetworkRoot,
                        executors);
                Logger.d("Made new repository");
            }
        }
        return sInstance;
    }

    public LiveData<List<Recipe>> getCurrentRecipes() {
        initializeData();
        return mRecipeDao.getAll();
    }

    public LiveData<List<Ingredient>> getIngredientsByRecipeId(int id) {
        initializeData();
        return mRecipeDao.getIngredients(id);
    }

    public LiveData<List<Step>> getStepsByRecipeId(int id) {
        initializeData();
        return mRecipeDao.getSteps(id);
    }

    private synchronized void initializeData() {
        // initialize once per lifetime
        if (mInitialized) return;
        mInitialized = true;

        // for services and jobs
//        mExecutors.diskIO().execute(() -> {
//            mRecipesNetworkRoot.getCurrentRecipes();
//        });
    }

}
