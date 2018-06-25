package com.example.aarta.tastybaking.ui.main;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.aarta.tastybaking.R;
import com.example.aarta.tastybaking.data.models.Recipe;
import com.example.aarta.tastybaking.ui.detail.DetailActivity;
import com.facebook.stetho.Stetho;
import com.orhanobut.logger.Logger;

public class MainActivity extends AppCompatActivity implements RecipesCardListFragment.onRecipeCardsListFragmentInteractionListener {

    public static final String KEY_SELECTED_RECIPE_ID = "selected_recipe_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupActionBar();
        Stetho.initializeWithDefaults(this);
    }

    public void setupActionBar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setLogo(R.mipmap.ic_launcher);
            getSupportActionBar().setDisplayUseLogoEnabled(true);
        }
    }

    @Override
    public void onCardListFragmentInteraction(Recipe recipe) {
        Intent detailActivityIntent = new Intent(this, DetailActivity.class);
        Bundle mainBundle = new Bundle();
        mainBundle.putInt(KEY_SELECTED_RECIPE_ID, recipe.getId());
        detailActivityIntent.putExtras(mainBundle);
        startActivity(detailActivityIntent);
    }

}
