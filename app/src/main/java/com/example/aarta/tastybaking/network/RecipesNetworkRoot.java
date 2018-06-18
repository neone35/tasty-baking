package com.example.aarta.tastybaking.network;

import android.annotation.SuppressLint;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;

import com.example.aarta.tastybaking.AppExecutors;
import com.example.aarta.tastybaking.models.Recipe;
import com.orhanobut.logger.Logger;

import java.util.List;

public class RecipesNetworkRoot {

    // Singleton instantiation
    private static final Object LOCK = new Object();
    @SuppressLint("StaticFieldLeak")
    private static RecipesNetworkRoot sInstance;

    // MutableLiveData with expected return type to notify all observers with postValue
    private final MutableLiveData<List<Recipe>> mDownloadedRecipes;
    private final AppExecutors mExecutors;

    private RecipesNetworkRoot(Context context, AppExecutors executors) {
        mExecutors = executors;
        mDownloadedRecipes = new MutableLiveData<List<Recipe>>();
    }

    // Get singleton for this class
    public static RecipesNetworkRoot getInstance(Context context, AppExecutors executors) {
        Logger.d("Getting the network data source");
        // Only one instance of this class can be created
        if (sInstance == null) {
            // and only one thread can access this method at a time for data consistency
            synchronized (LOCK) {
                sInstance = new RecipesNetworkRoot(context.getApplicationContext(), executors);
                Logger.d("Made new network data source");
            }
        }
        return sInstance;
    }

    public LiveData<List<Recipe>> getCurrentRecipes() {
        return mDownloadedRecipes;
    }

    void fetchRecipes() {
        Logger.d("Recipe fetch started");
        mExecutors.networkIO().execute(() -> {
            try {
                List<Recipe> recipes = NetworkUtils.getResponseWithUrl(NetworkUtils.RECIPES_URL, NetworkUtils.RECIPES_JSON_NAME);

                // trigger observers of that LiveData
                if (recipes != null && recipes.size() != 0) {
                    Logger.d("JSON not null and has " + recipes.size() + " values");
                    Logger.d("First value is " + recipes.get(0));

                    // update LiveData off main thread -> to main thread (postValue)
                    mDownloadedRecipes.postValue(recipes);

                }
            } catch (Exception e) {
                // Server probably invalid
                e.printStackTrace();
            }
        });
    }
}
