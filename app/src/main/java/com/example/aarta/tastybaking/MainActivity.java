package com.example.aarta.tastybaking;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.aarta.tastybaking.databinding.ActivityMainBinding;
import com.example.aarta.tastybaking.models.Recipe;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

public class MainActivity extends AppCompatActivity implements RecipeCardListFragment.onRecipeCardsListFragmentInteractionListener {

    public static final String RECIPES_JSON_URL = "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/";
    private ActivityMainBinding mainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Logger.addLogAdapter(new AndroidLogAdapter());
        RecipeCardListFragment recipeCardListFragment = RecipeCardListFragment.newInstance(2);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.fl_recipe_cards_fragment, recipeCardListFragment)
                .commit();
    }

    @Override
    public void onListFragmentInteraction(Recipe recipe) {
        Logger.d(recipe);
    }
}
