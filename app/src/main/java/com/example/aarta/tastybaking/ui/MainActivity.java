package com.example.aarta.tastybaking.ui;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.aarta.tastybaking.R;
import com.example.aarta.tastybaking.data.network.RecipesNetworkRoot;
import com.example.aarta.tastybaking.data.models.Recipe;
import com.example.aarta.tastybaking.utils.InjectorUtils;
import com.facebook.stetho.Stetho;
import com.orhanobut.logger.Logger;

public class MainActivity extends AppCompatActivity implements RecipeCardListFragment.onRecipeCardsListFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Stetho.initializeWithDefaults(this);
        setContentView(R.layout.activity_main);
//        Logger.addLogAdapter(new AndroidLogAdapter());
        RecipeCardListFragment recipeCardListFragment = RecipeCardListFragment.newInstance(2);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.fl_recipe_cards_fragment, recipeCardListFragment)
                .commit();
        fetchNewRecipes();
    }

    @Override
    public void onListFragmentInteraction(Recipe recipe) {
        Logger.d(recipe);
    }

    public void fetchNewRecipes() {
        RecipesNetworkRoot recipesNetworkRoot = InjectorUtils.provideNetworkDataSource(this);
        recipesNetworkRoot.fetchRecipes();
    }
}
