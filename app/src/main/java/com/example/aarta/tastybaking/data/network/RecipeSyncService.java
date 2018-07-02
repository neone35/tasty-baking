package com.example.aarta.tastybaking.data.network;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.example.aarta.tastybaking.utils.InjectorUtils;

import timber.log.Timber;

public class RecipeSyncService extends IntentService {

    private static final String CLASS_NAME = "RecipeSyncService";

    public RecipeSyncService() {
        super(CLASS_NAME);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Timber.d("Intent service started");
        RecipesNetworkRoot recipesNetworkRoot = InjectorUtils.provideNetworkDataSource(this.getApplicationContext());
        recipesNetworkRoot.fetchRecipes();
    }
}
