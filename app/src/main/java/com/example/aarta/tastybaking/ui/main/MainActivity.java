package com.example.aarta.tastybaking.ui.main;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.aarta.tastybaking.BuildConfig;
import com.example.aarta.tastybaking.NotLoggingTree;
import com.example.aarta.tastybaking.R;
import com.example.aarta.tastybaking.data.models.Recipe;
import com.example.aarta.tastybaking.ui.detail.DetailActivity;
import com.example.aarta.tastybaking.widgets.IngredientsWidgetProvider;
import com.facebook.stetho.Stetho;

import java.util.Objects;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements RecipesCardListFragment.onRecipeCardsListFragmentInteractionListener {

    public static final String KEY_SELECTED_RECIPE_ID = "selected_recipe_id";
    private static final String INGREDIENT_PREFS_NAME
            = "com.example.aarta.tastybaking.widgets.IngredientsWidgetProvider";
    private static final String INGREDIENT_PREF_KEY = "ingr_widget_";
    private int mIngredientWidgetID = -1;
    private Intent mConfigResultIntent;

    // WIDGET PREFERENCE HELPERS
    // Write RecipeID into SharedPreferences for particular widget
    private static void saveRecipeIDPref(Context context, int appWidgetId, int recipeID) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(INGREDIENT_PREFS_NAME, 0).edit();
        String WIDGET_PREF_KEY = INGREDIENT_PREF_KEY + appWidgetId;
        prefs.putInt(WIDGET_PREF_KEY, recipeID);
        prefs.apply();
        Timber.d("Preference " + WIDGET_PREF_KEY + " with recipeID " + recipeID + " successfully saved");
    }

    // Read RecipeID from SharedPreferences for particular widget
    public static int loadRecipeIDPref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(INGREDIENT_PREFS_NAME, 0);
        String WIDGET_PREF_KEY = INGREDIENT_PREF_KEY + appWidgetId;
        int recipeID = prefs.getInt(WIDGET_PREF_KEY, -1);
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
            Timber.d("Preference key " + WIDGET_PREF_KEY + " deleted");
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
        debugConfig();
        // setup activity
        setContentView(R.layout.activity_main);
        setupActionBar();
        // on mobile, lock into portrait
        if (this.findViewById(R.id.cards_list_holder_tablet) == null) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        // This activity can be ingredient widget config activity.
        // Don't add widget if activity closed before recipe selection
        if (getIntent() != null) {
            mIngredientWidgetID = getIntent().getIntExtra(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
            if (mIngredientWidgetID != AppWidgetManager.INVALID_APPWIDGET_ID && mIngredientWidgetID != -1) {
                Objects.requireNonNull(this.getSupportActionBar()).setTitle("Select a recipe");
                mConfigResultIntent = new Intent();
                mConfigResultIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mIngredientWidgetID);
                // inform widget which launched this activity that result has NOT been set
                setResult(RESULT_CANCELED, mConfigResultIntent);
            }
        }
    }

    private void debugConfig() {
        // Timber logging config
        if (BuildConfig.DEBUG)
            Timber.plant(new Timber.DebugTree());
        else
            Timber.plant(new NotLoggingTree());
        // Database debugging
        Stetho.initializeWithDefaults(this);
    }

    @Override
    public void onCardListFragmentInteraction(Recipe recipe) {
        int selectedRecipeID = recipe.getId();
        // check if widgetID really exists (not 0 or -1)
        if (mConfigResultIntent != null) {
            int configWidgetID = mConfigResultIntent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
            if (configWidgetID != -1 && configWidgetID != AppWidgetManager.INVALID_APPWIDGET_ID) {
                // save the recipeID into prefs associated with widgetID
                saveRecipeIDPref(this, mIngredientWidgetID, selectedRecipeID);
                // update widget layout with newly set recipeID
                // recipeID is loaded with loadRecipeIDPref in updateIngredientWidget
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
                IngredientsWidgetProvider.updateIngredientWidget(this, appWidgetManager, mIngredientWidgetID);
                // inform system that config is finished and close activity
                setResult(RESULT_OK, mConfigResultIntent);
                finish();
            }
        } else {
            // launch detail activity as default
            Intent detailActivityIntent = new Intent(this, DetailActivity.class);
            detailActivityIntent.putExtra(KEY_SELECTED_RECIPE_ID, selectedRecipeID);
            startActivity(detailActivityIntent);
        }
    }

}
