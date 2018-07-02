package com.example.aarta.tastybaking.ui.main;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.aarta.tastybaking.BuildConfig;
import com.example.aarta.tastybaking.NotLoggingTree;
import com.example.aarta.tastybaking.R;
import com.example.aarta.tastybaking.data.models.Recipe;
import com.example.aarta.tastybaking.ui.detail.DetailActivity;
import com.example.aarta.tastybaking.widgets.IngredientsWidgetProvider;
import com.facebook.stetho.Stetho;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements RecipesCardListFragment.onRecipeCardsListFragmentInteractionListener {

    public static final String KEY_SELECTED_RECIPE_ID = "selected_recipe_id";
    private static final String INGREDIENT_PREFS_NAME
            = "com.example.aarta.tastybaking.widgets.IngredientsWidgetProvider";
    private static final String INGREDIENT_PREF_KEY = "ingr_widget_";
    private int mIngredientWidgetID = -1;

    // WIDGET HELPERS
    // Write RecipeID into SharedPreferences for particular widget
    private static void saveRecipeIDPref(Context context, int appWidgetId, int recipeID) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(INGREDIENT_PREFS_NAME, 0).edit();
        prefs.putInt(INGREDIENT_PREF_KEY + appWidgetId, recipeID);
        prefs.apply();
    }

    // Read RecipeID from SharedPreferences for particular widget
    public static int loadRecipeIDPref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(INGREDIENT_PREFS_NAME, 0);
        int recipeID = prefs.getInt(INGREDIENT_PREF_KEY + appWidgetId, -1);
        if (recipeID != -1) {
            return recipeID;
        } else {
            return -1;
        }
    }

    // delete RecipeID associated with particular widgetID from preferences
    public static void deleteRecipeIDPref(Context context, int appWidgetId) {
        SharedPreferences ingredientWidgetPrefs = context.getSharedPreferences(INGREDIENT_PREFS_NAME, 0);
        SharedPreferences.Editor prefsEditor = ingredientWidgetPrefs.edit();
        String WIDGET_PREF_KEY = INGREDIENT_PREF_KEY + appWidgetId;
        int recipeID = ingredientWidgetPrefs.getInt(WIDGET_PREF_KEY, -1);
        if (recipeID != -1) {
            prefsEditor.remove(WIDGET_PREF_KEY);
            prefsEditor.apply();
        } else {
            Timber.e("Preference key " + WIDGET_PREF_KEY + " not found");
        }
    }

    public void setupActionBar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setLogo(R.mipmap.ic_launcher);
            getSupportActionBar().setDisplayUseLogoEnabled(true);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Timber logging config
        if (BuildConfig.DEBUG)
            Timber.plant(new Timber.DebugTree());
        else
            Timber.plant(new NotLoggingTree());
        // Database debugging
        Stetho.initializeWithDefaults(this);

        setContentView(R.layout.activity_main);
        setupActionBar();

        // This activity is ingredient widget config activity
        // don't add widget if activity closed before recipe selection
        if (getIntent().getExtras() != null) {
            mIngredientWidgetID = getIntent().getExtras().getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
            if (mIngredientWidgetID != AppWidgetManager.INVALID_APPWIDGET_ID && mIngredientWidgetID != -1) {
                Intent configResultIntent = new Intent();
                configResultIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mIngredientWidgetID);
                // inform widget which launched this activity that result has NOT been set
                setResult(RESULT_CANCELED, configResultIntent);
            }
        }
    }

    @Override
    public void onCardListFragmentInteraction(Recipe recipe) {
        int selectedRecipeID = recipe.getId();
        // get widgetID if activity launched as widget config
        if (getIntent().getExtras() != null)
            mIngredientWidgetID = getIntent().getExtras().getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        // check if widgetID really exists (not 0 or -1)
        if (mIngredientWidgetID != AppWidgetManager.INVALID_APPWIDGET_ID && mIngredientWidgetID != -1) {
            // save the recipeID into prefs associated with widgetID
            saveRecipeIDPref(this, mIngredientWidgetID, selectedRecipeID);

            // update widget layout with newly set recipeID
            // recipeID is loaded with loadRecipeIDPref in updateIngredientWidget
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
            IngredientsWidgetProvider.updateIngredientWidget(this, appWidgetManager, mIngredientWidgetID);

            // inform system that config is finished and close activity
            Intent configResultIntent = new Intent();
            configResultIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mIngredientWidgetID);
            setResult(RESULT_OK, configResultIntent);
            finish();
        } else {
            // launch detail activity as default
            Intent detailActivityIntent = new Intent(this, DetailActivity.class);
            Bundle mainBundle = new Bundle();
            mainBundle.putInt(KEY_SELECTED_RECIPE_ID, selectedRecipeID);
            detailActivityIntent.putExtras(mainBundle);
            startActivity(detailActivityIntent);
        }
    }

}
