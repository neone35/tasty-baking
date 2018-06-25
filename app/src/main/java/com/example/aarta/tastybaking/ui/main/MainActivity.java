package com.example.aarta.tastybaking.ui.main;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.aarta.tastybaking.R;
import com.example.aarta.tastybaking.data.models.Recipe;
import com.example.aarta.tastybaking.databinding.ActivityMainBinding;
import com.example.aarta.tastybaking.ui.detail.DetailActivity;
import com.facebook.stetho.Stetho;

public class MainActivity extends AppCompatActivity implements RecipesCardListFragment.onRecipeCardsListFragmentInteractionListener {

    public static final String KEY_SELECTED_RECIPE_ID = "selected_recipe_id";
    ActivityMainBinding mainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        setActionBar("Tasty baking");
        Stetho.initializeWithDefaults(this);

        // new card list fragment instance with number of columns for LayoutManager
        // create it only if device had no configuration change
        if (savedInstanceState == null) {
            RecipesCardListFragment recipesCardListFragment;
            FragmentManager fragmentManager = getSupportFragmentManager();
            int tabletFragmentHolderID = R.id.fl_cards_list_holder_tablet;
            int mobileFragmentHolderID = R.id.fl_cards_list_holder;
            if (findViewById(tabletFragmentHolderID) != null) {
                recipesCardListFragment = RecipesCardListFragment.newInstance(3);
                fragmentManager.beginTransaction()
                        .add(tabletFragmentHolderID, recipesCardListFragment)
                        .commit();
            } else {
                recipesCardListFragment = RecipesCardListFragment.newInstance(1);
                fragmentManager.beginTransaction()
                        .add(mobileFragmentHolderID, recipesCardListFragment)
                        .commit();
            }
        }
    }

    public void setActionBar(String heading) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(heading);
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
