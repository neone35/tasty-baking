package com.example.aarta.tastybaking.ui.detail;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.aarta.tastybaking.R;
import com.example.aarta.tastybaking.data.models.Recipe;
import com.example.aarta.tastybaking.data.models.Step;
import com.example.aarta.tastybaking.databinding.ActivityDetailBinding;
import com.example.aarta.tastybaking.ui.main.MainActivity;
import com.example.aarta.tastybaking.ui.step.StepActivity;
import com.example.aarta.tastybaking.ui.step.StepFragment;
import com.example.aarta.tastybaking.utils.InjectorUtils;
import com.orhanobut.logger.Logger;

import java.util.Objects;

public class DetailActivity extends AppCompatActivity implements DetailListFragment.onDetailListFragmentInteractionListener {

    private static int RECIPE_ID;
    private ActivityDetailBinding detailBinding;
    public static final String STEPS_MODE = "show_steps";
    public static final String INGREDIENTS_MODE = "show_ingredients";
    private FragmentManager fragmentManager;
    private static int backStackNum;
    private static String ingredients;
    private static String steps;
    private static String seeIngredients;
    private static String seeSteps;
    public static final String KEY_SELECTED_STEP_ID = "selected_step_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        detailBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);
        fragmentManager = getSupportFragmentManager();
        hideRecipeDetailImage();

        // get needed resources
        ingredients = getResources().getString(R.string.ingredients).substring(0, 1).toUpperCase() +
                getResources().getString(R.string.ingredients).substring(1);
        steps = getResources().getString(R.string.steps).substring(0, 1).toUpperCase() +
                getResources().getString(R.string.steps).substring(1);
        seeIngredients = "see " + getResources().getString(R.string.ingredients);
        seeSteps = "see " + getResources().getString(R.string.steps);

        // check if intent bundle received successfully
        Bundle mainExtrasBundle = getIntent().getExtras();
        if (mainExtrasBundle != null) {
            RECIPE_ID = mainExtrasBundle.getInt(MainActivity.KEY_SELECTED_RECIPE_ID);
        }

        // setup view using RECIPE_ID from bundle (parent) or field (child)
        setOneRecipeInfo(RECIPE_ID);
        // only create initial fragment if there was no configuration change
        if (savedInstanceState == null) {
            // add initial fragment
            DetailListFragment stepsListFragment = DetailListFragment.newInstance(RECIPE_ID, STEPS_MODE);
            fragmentManager.beginTransaction()
                    .add(R.id.fl_detail_list_holder, stepsListFragment)
                    .commit();
            // set initial mode
            switchFragment(seeIngredients, steps, STEPS_MODE);
        }
        // always listen for fragment back stack num change
        backStackNumListener();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (backStackNum == 0) {
            // steps fragment was last
            outState.putInt(STEPS_MODE, backStackNum);
        } else {
            // ingredients fragment was last
            outState.putInt(INGREDIENTS_MODE, backStackNum);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // switch to last fragment
        if (savedInstanceState.containsKey(STEPS_MODE))
            switchFragment(seeIngredients, steps, STEPS_MODE);
        else if (savedInstanceState.containsKey(INGREDIENTS_MODE))
            switchFragment(seeSteps, ingredients, INGREDIENTS_MODE);
    }

    public void switchFragment(String buttonText, String actionBarTitle, String mode) {
//        Logger.d(mode);
        // change UI
        Objects.requireNonNull(getSupportActionBar()).setTitle(actionBarTitle);
        detailBinding.btnIngredients.setText(buttonText);
        // replace fragment on click
        switch (mode) {
            case STEPS_MODE:
                detailBinding.btnIngredients.setOnClickListener(v -> {
                    // switch to ingredients on click
                    DetailListFragment ingredientListFragment = DetailListFragment.newInstance(RECIPE_ID, INGREDIENTS_MODE);
                    fragmentManager.beginTransaction()
                            .replace(R.id.fl_detail_list_holder, ingredientListFragment)
                            // add it to back stack (set to 1)
                            .addToBackStack(INGREDIENTS_MODE)
                            .commit();
                });
                break;
            case INGREDIENTS_MODE:
                detailBinding.btnIngredients.setOnClickListener(v -> {
                    // erase back stack (set to 0)
                    fragmentManager.popBackStack();
                    // switch to steps fragment on click
                    DetailListFragment stepsListFragment = DetailListFragment.newInstance(RECIPE_ID, STEPS_MODE);
                    fragmentManager.beginTransaction()
                            .replace(R.id.fl_detail_list_holder, stepsListFragment)
                            .commit();
                });
                break;
        }
    }

    private void backStackNumListener() {
//        Logger.d(backStackNum);
        // listen for backStack change
        fragmentManager.addOnBackStackChangedListener(() -> {
            backStackNum = fragmentManager.getBackStackEntryCount();
//            Logger.d(backStackNum);
            if (backStackNum == 0) {
                // steps has been clicked
                switchFragment(seeIngredients, steps, STEPS_MODE);
            } else {
                // ingredients has been clicked
                switchFragment(seeSteps, ingredients, INGREDIENTS_MODE);
            }
        });
    }

    private void hideRecipeDetailImage() {
        // hide recipe image in detail only
        detailBinding.incRecipeInfo.ivRecipe.setVisibility(View.INVISIBLE);
    }

    private void setOneRecipeInfo(int recipeID) {
        // Get repository instance
        OneRecipeViewModelFactory factory = InjectorUtils.provideDetailViewModelFactory(this, recipeID);
        // Tie fragment & ViewModel together
        OneRecipeViewModel mViewModel = ViewModelProviders.of(this, factory).get(OneRecipeViewModel.class);
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

    @Override
    public void onDetailListFragmentInteraction(Recipe selectedRecipe, Step selectedStep) {
        // mobile layout
        if (findViewById(R.id.ll_detail_tablet) == null) {
            Intent stepActivityIntent = new Intent(this, StepActivity.class);
            Bundle detailBundle = new Bundle();
            detailBundle.putInt(MainActivity.KEY_SELECTED_RECIPE_ID, selectedRecipe.getId());
            detailBundle.putInt(KEY_SELECTED_STEP_ID, selectedStep.getId());
            stepActivityIntent.putExtras(detailBundle);
            startActivity(stepActivityIntent);
        } else { // tablet layout
            StepFragment stepsListFragment = StepFragment.newInstance(RECIPE_ID, selectedStep.getId());
            fragmentManager.beginTransaction()
                    .replace(R.id.fl_step_fragment_holder, stepsListFragment)
                    .commit();
        }
    }
}
