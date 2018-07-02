package com.example.aarta.tastybaking.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.widget.RemoteViews;

import com.example.aarta.tastybaking.AppExecutors;
import com.example.aarta.tastybaking.R;
import com.example.aarta.tastybaking.data.database.RecipeDao;
import com.example.aarta.tastybaking.data.database.RecipeDatabase;
import com.example.aarta.tastybaking.ui.detail.DetailActivity;
import com.example.aarta.tastybaking.ui.main.MainActivity;

import timber.log.Timber;

// called during widget updates (or add)
public class IngredientsWidgetProvider extends AppWidgetProvider {

    private static Cursor mRecipeCursor;

    // called from config activity (MainActivity)
    public static void updateIngredientWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {

        // read recipeID from preferences associated with this widgetID
        // it is saved on recipe selection
        int selectedRecipeID = MainActivity.loadRecipeIDPref(context, appWidgetId);

        // assign recipeID to widgetID (first time from config)
        if (selectedRecipeID != -1) {
            Timber.d("Updating widgetID " + appWidgetId + " with recipeID " + selectedRecipeID + " in updateIngredientWidget");

            // receive Recipe cursor from DB
            RecipeDatabase database = RecipeDatabase.getInstance(context);
            AppExecutors executors = AppExecutors.getInstance();
            executors.diskIO().execute(() -> {
                RecipeDao recipeDao = database.recipeDao();
                mRecipeCursor = recipeDao.getStaticRecipeCursor(selectedRecipeID);
                if (mRecipeCursor != null) {
                    // set Recipe name into remote TextView
                    RemoteViews ingredientWidgetView = new RemoteViews(context.getPackageName(), R.layout.ingredient_widget_layout);
                    mRecipeCursor.moveToFirst();
                    String recipeName = mRecipeCursor.getString(mRecipeCursor.getColumnIndex("name"));
                    ingredientWidgetView.setTextViewText(R.id.tv_ingredient_widget_recipe_title, recipeName);

                    // set remote adapter into remote ListView
                    Intent adapterIntent = new Intent(context, IngredientListWidgetService.class);
                    adapterIntent.putExtra(MainActivity.KEY_SELECTED_RECIPE_ID, selectedRecipeID);
                    //set new data to initialize new factory (different data on separate widgets)
                    adapterIntent.setData(Uri.parse(adapterIntent.toUri(Intent.URI_INTENT_SCHEME)));
                    ingredientWidgetView.setRemoteAdapter(R.id.lv_ingredient_widget_list, adapterIntent);

                    // create intent to launch detail activity if clicked on recipe title or ingredient list
                    Intent detailActivityIntent = new Intent(context, DetailActivity.class);
                    PendingIntent detailPendingIntent = PendingIntent.getActivity(context, 0, detailActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    detailActivityIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
                    detailActivityIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
                    detailActivityIntent.putExtra(MainActivity.KEY_SELECTED_RECIPE_ID, selectedRecipeID);
                    // set click listeners on widget views to launch detail activity
                    ingredientWidgetView.setOnClickPendingIntent(R.id.ll_ingredient_widget_title_holder, detailPendingIntent);
                    ingredientWidgetView.setOnClickPendingIntent(R.id.ll_ingredients_widget, detailPendingIntent);
                    ingredientWidgetView.setPendingIntentTemplate(R.id.lv_ingredient_widget_list, detailPendingIntent);

                    // finally update widget with widget manager
                    appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.lv_ingredient_widget_list);
                    appWidgetManager.updateAppWidget(appWidgetId, ingredientWidgetView);
                } else {
                    Timber.e("Recipe with ID " + selectedRecipeID + " is null");
                }
            });
        } else {
            Timber.e("RecipeID " + selectedRecipeID + " not found in widget preferences");
        }
    }

    // called every updatePeriodMillis specified in widget_info && on addition
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        for (int ingredientsWidgetId : appWidgetIds) {
            // read recipeID from preferences associated with this widgetID
            int selectedRecipeID = MainActivity.loadRecipeIDPref(context, ingredientsWidgetId);
            // update all widgets associated with recipeID
            if (selectedRecipeID != -1) {
                Timber.d("Updating widgetID " + ingredientsWidgetId + " with recipeID " + selectedRecipeID + " in onUpdate");
                RemoteViews ingredientWidgetView = new RemoteViews(context.getPackageName(), R.layout.ingredient_widget_layout);
                updateIngredientWidget(context, appWidgetManager, ingredientsWidgetId);
                // update current app widget
                appWidgetManager.updateAppWidget(ingredientsWidgetId, ingredientWidgetView);
            }
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        for (int widgetId : appWidgetIds) {
            // read recipeID from preferences associated with this widgetID
            int selectedRecipeID = MainActivity.loadRecipeIDPref(context, widgetId);
            if (selectedRecipeID != -1) {
                // delete all widgets associated with recipeID
                MainActivity.deleteRecipeIDPref(context, widgetId);
            }
        }
    }
}
