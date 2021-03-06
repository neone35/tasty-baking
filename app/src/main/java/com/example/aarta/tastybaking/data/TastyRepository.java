package com.example.aarta.tastybaking.data;

import android.arch.lifecycle.LiveData;

import com.example.aarta.tastybaking.AppExecutors;
import com.example.aarta.tastybaking.data.database.RecipeDao;
import com.example.aarta.tastybaking.data.models.Recipe;
import com.example.aarta.tastybaking.data.network.RecipesNetworkRoot;
import com.example.aarta.tastybaking.utils.RecipeUtils;

import java.util.List;

import timber.log.Timber;

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

        // While repository exists, observe MutableLiveData and insert values on postValue call
        LiveData<List<Recipe>> downloadedRecipes = mRecipesNetworkRoot.getDownloadedRecipes();
        downloadedRecipes.observeForever(newRecipesFromNetwork -> mExecutors.diskIO().execute(() -> {
            // Delete old recipes if present
            // Triggered only if database is filled
//            if (mRecipeDao.getAll() != null) {
//                mRecipeDao.deleteAllRecipes();
//                Timber.d("Old recipes deleted");
//            }

            // Insert new recipes into recipeDB (DAO LiveData observers get notified)
            // Triggered only if fetch has been made (from service). Use offline data otherwise
            Recipe[] recipeArray;
            if (newRecipesFromNetwork != null) {
                recipeArray = RecipeUtils.getRecipeArray(newRecipesFromNetwork);
                // DB changes are detected by LiveData DB monitor (getCurrentRecipes)
                mRecipeDao.bulkInsert(recipeArray);
                Timber.d("New values inserted");
            } else {
                Timber.d("No response from network");
            }
        }));
    }

    public synchronized static TastyRepository getInstance(
            RecipeDao recipeDao, RecipesNetworkRoot recipesNetworkRoot,
            AppExecutors executors) {
//        Timber.d("Getting the repository");
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new TastyRepository(recipeDao, recipesNetworkRoot,
                        executors);
                Timber.d("Made new repository");
            }
        }
        return sInstance;
    }

    // called from MainFragViewModel
    public LiveData<List<Recipe>> getCurrentRecipes() {
//        Timber.d("Getting all recipes");
        // start fetch in service (once)
        initializeData();
        // get recipe list from DB
        return mRecipeDao.getAll();
    }

    public LiveData<Recipe> getRecipeById(int id) {
//        Timber.d("Getting one recipe");
        initializeData();
        return mRecipeDao.getById(id);
    }

    private synchronized void initializeData() {
        // initialize (& fetch) once per lifetime
        if (mInitialized) return;
        mInitialized = true;

        // start background service to immediately fetch recipes and notify all observers
        mExecutors.diskIO().execute(mRecipesNetworkRoot::startRecipeFetchService);
    }
}
