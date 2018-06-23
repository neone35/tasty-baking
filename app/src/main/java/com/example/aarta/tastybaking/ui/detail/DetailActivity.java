package com.example.aarta.tastybaking.ui.detail;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.aarta.tastybaking.R;
import com.example.aarta.tastybaking.data.models.Ingredient;
import com.example.aarta.tastybaking.data.models.Step;
import com.example.aarta.tastybaking.databinding.ActivityDetailBinding;
import com.example.aarta.tastybaking.ui.main.MainActivity;
import com.example.aarta.tastybaking.utils.InjectorUtils;
import com.orhanobut.logger.Logger;

import java.util.List;

public class DetailActivity extends AppCompatActivity implements DetailListFragment.onDetailListFragmentInteractionListener {

    private static int RECIPE_ID;
    private ActivityDetailBinding detailBinding;
    public static final String SHOW_STEPS = "show_steps";
    public static final String SHOW_INGREDIENTS = "show_ingredients";
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        detailBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);
        fragmentManager = getSupportFragmentManager();

        // check if intent bundle received successfully and setup view
        Bundle mainExtrasBundle = getIntent().getExtras();
        if (mainExtrasBundle != null) {
            RECIPE_ID = mainExtrasBundle.getInt(MainActivity.KEY_SELECTED_RECIPE_ID);
            setupRecipeDetailView();
            setOneRecipeInfo(RECIPE_ID);
            // don't create fragment if there was configuration change
            if (savedInstanceState == null) {
                // add initial fragment
                DetailListFragment stepsListFragment = DetailListFragment.newInstance(RECIPE_ID, SHOW_STEPS);
                fragmentManager.beginTransaction()
                        .add(R.id.fl_detail_list_holder, stepsListFragment)
                        .commit();
            }
            backStackListener();
        }
    }

    public void setStepMode() {
        // change UI
        String ingredients = getResources().getString(R.string.ingredients);
        String steps = getResources().getString(R.string.steps);
        setActionBar(steps);
        detailBinding.btnIngredients.setText(ingredients);
        // replace with ingredients fragment on click
        detailBinding.btnIngredients.setOnClickListener(v -> {
            DetailListFragment ingredientListFragment = DetailListFragment.newInstance(RECIPE_ID, SHOW_INGREDIENTS);
            fragmentManager.beginTransaction()
                    .replace(R.id.fl_detail_list_holder, ingredientListFragment)
                    // set backStackNum to 1
                    .addToBackStack(SHOW_INGREDIENTS)
                    .commit();
        });
    }

    public void setIngredientMode() {
        // change UI
        String ingredients = getResources().getString(R.string.ingredients);
        String steps = getResources().getString(R.string.steps);
        setActionBar(ingredients);
        detailBinding.btnIngredients.setText(steps);
        // replace with steps fragment on click
        detailBinding.btnIngredients.setOnClickListener(v -> {
            // set backStackNum to 0
            fragmentManager.popBackStack();
            DetailListFragment stepsListFragment = DetailListFragment.newInstance(RECIPE_ID, SHOW_STEPS);
            fragmentManager.beginTransaction()
                    .replace(R.id.fl_detail_list_holder, stepsListFragment)
                    .commit();
        });
    }

    private void backStackListener() {
        // set initial mode
        setStepMode();
        // listen for backStack change
        fragmentManager.addOnBackStackChangedListener(() -> {
            int backStackNum = fragmentManager.getBackStackEntryCount();
            Logger.d(backStackNum);
            if (backStackNum == 1) {
                // listen for steps click | populate with ingredients
                setIngredientMode();
            } else if (backStackNum == 0) {
                // listen for ingredients click | populate with steps
                setStepMode();
            }
        });
    }

    private void setupRecipeDetailView() {
        // hide recipe image in detail only
        detailBinding.incRecipeInfo.ivRecipe.setVisibility(View.INVISIBLE);
    }

    private void setOneRecipeInfo(int recipeID) {
        // Get repository instance
        DetailViewModelFactory factory = InjectorUtils.provideDetailViewModelFactory(this, recipeID);
        // Tie fragment & ViewModel together
        DetailViewModel mViewModel = ViewModelProviders.of(this, factory).get(DetailViewModel.class);
        mViewModel.getOneRecipe().observe(this, oneRecipe -> {
            if (oneRecipe != null) {
                String recipeName = oneRecipe.getName();
                String ingredientNum = String.valueOf(oneRecipe.getIngredients().size());
                String stepsNum = String.valueOf(oneRecipe.getSteps().size());
                String servingsNum = String.valueOf(oneRecipe.getServings());
                detailBinding.incRecipeInfo.tvRecipeName.setText(recipeName);
                detailBinding.incRecipeInfo.tvIngredientNum.setText(ingredientNum);
                detailBinding.incRecipeInfo.tvStepsNum.setText(stepsNum);
                detailBinding.incRecipeInfo.tvServingsNum.setText(servingsNum);
            }
        });
    }

    public void setActionBar(String heading) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(heading);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }


    @Override
    public void onDetailListFragmentInteraction(Step step) {
        Logger.d(step.getDescription());
    }
}
