package com.example.aarta.tastybaking.utils;

import android.content.Context;

import com.example.aarta.tastybaking.AppExecutors;
import com.example.aarta.tastybaking.data.TastyRepository;
import com.example.aarta.tastybaking.data.database.RecipeDatabase;
import com.example.aarta.tastybaking.data.network.RecipesNetworkRoot;
import com.example.aarta.tastybaking.ui.detail.DetailViewModelFactory;
import com.example.aarta.tastybaking.ui.main.MainViewModelFactory;

public class InjectorUtils {

    private static TastyRepository provideRepository(Context context) {
        RecipeDatabase database = RecipeDatabase.getInstance(context.getApplicationContext());
        AppExecutors executors = AppExecutors.getInstance();
        RecipesNetworkRoot networkDataSource =
                RecipesNetworkRoot.getInstance(context.getApplicationContext(), executors);
        return TastyRepository.getInstance(database.recipeDao(), networkDataSource, executors);
    }

    // for services and jobs (external access)
    public static RecipesNetworkRoot provideNetworkDataSource(Context context) {
        provideRepository(context.getApplicationContext());
        AppExecutors executors = AppExecutors.getInstance();
        return RecipesNetworkRoot.getInstance(context.getApplicationContext(), executors);
    }

    public static MainViewModelFactory provideMainViewModelFactory(Context context) {
        TastyRepository repository = provideRepository(context.getApplicationContext());
        return new MainViewModelFactory(repository);
    }

    public static DetailViewModelFactory provideDetailViewModelFactory(Context context, int recipeID) {
        TastyRepository repository = provideRepository(context.getApplicationContext());
        return new DetailViewModelFactory(repository, recipeID);
    }
}
