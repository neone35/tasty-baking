package com.example.aarta.tastybaking.widgets;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.aarta.tastybaking.R;
import com.example.aarta.tastybaking.data.database.RecipeDao;
import com.example.aarta.tastybaking.data.database.RecipeDatabase;
import com.example.aarta.tastybaking.data.models.Ingredient;
import com.example.aarta.tastybaking.data.models.Recipe;
import com.example.aarta.tastybaking.ui.main.MainActivity;
import com.google.gson.Gson;

import java.util.List;

import timber.log.Timber;

public class IngredientListWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        int recipeID = -1;
        if (intent.getExtras() != null)
            recipeID = intent.getExtras().getInt(MainActivity.KEY_SELECTED_RECIPE_ID, -1);
        return new IngredientListRemoteViewsFactory(this.getApplicationContext(), recipeID);
    }
}

class IngredientListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private static Gson gson = new Gson();
    private Context mContext;
    private List<Ingredient> mIngredientList;
    private int mRecipeID;

    IngredientListRemoteViewsFactory(Context appCtx, int recipeID) {
        mContext = appCtx;
        mRecipeID = recipeID;
    }

    private void getIngredientsFromDB(int recipeID) {
        RecipeDatabase mRecipeDB = RecipeDatabase.getInstance(mContext);
        Timber.d("Getting ingredients with RecipeID %s", recipeID);
        RecipeDao recipeDao = mRecipeDB.recipeDao();
        Recipe oneRecipe = recipeDao.getStaticRecipe(recipeID);
        mIngredientList = oneRecipe.getIngredients();
        Timber.d("First ingredient is %s", mIngredientList.get(0).getIngredient());
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onDataSetChanged() {
        getIngredientsFromDB(mRecipeID);
    }

    @Override
    public int getCount() {
        if (mIngredientList == null) return 0;
        return mIngredientList.size();
    }

    @Override
    public RemoteViews getViewAt(int pos) {
        if (mIngredientList == null || getCount() == 0) return null;

        // get ingredient list item view
        RemoteViews ingredientItemView = new RemoteViews(mContext.getPackageName(), R.layout.ingredient_widget_item);

        // get and set view values
        String ingredientName = pos + 1 + ". " + mIngredientList.get(pos).getIngredient();
        String ingredientQuantity = String.valueOf(mIngredientList.get(pos).getQuantity());
        String ingredientMeasure = mIngredientList.get(pos).getMeasure();
        ingredientItemView.setTextViewText(R.id.tv_ingredient_widget_label, ingredientName);
        ingredientItemView.setTextViewText(R.id.tv_ingredient_widget_quantity, ingredientQuantity);
        ingredientItemView.setTextViewText(R.id.tv_ingredient_widget_measure, ingredientMeasure);

        // set on click template to launch details activity
        Bundle extras = new Bundle();
        extras.putInt(MainActivity.KEY_SELECTED_RECIPE_ID, mRecipeID);
        Intent fillInIntent = new Intent();
        fillInIntent.putExtras(extras);
        ingredientItemView.setOnClickFillInIntent(R.id.rl_ingredient_widget_item, fillInIntent);

        return ingredientItemView;
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}
