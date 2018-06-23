package com.example.aarta.tastybaking.ui.detail;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.aarta.tastybaking.R;
import com.example.aarta.tastybaking.data.models.Step;
import com.example.aarta.tastybaking.databinding.ActivityDetailBinding;
import com.example.aarta.tastybaking.ui.main.MainActivity;
import com.example.aarta.tastybaking.utils.InjectorUtils;
import com.orhanobut.logger.Logger;

public class DetailActivity extends AppCompatActivity implements StepsListFragment.onDetailListFragmentInteractionListener {

    private static int RECIPE_ID;
    private ActivityDetailBinding detailBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        detailBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);

        // check if intent bundle received successfully and setup view
        Bundle mainExtrasBundle = getIntent().getExtras();
        if (mainExtrasBundle != null) {
            RECIPE_ID = mainExtrasBundle.getInt(MainActivity.KEY_SELECTED_RECIPE_ID);
            setupRecipeDetailView();
            setMainRecipeInfo();
        }

        // create fragment only if there was no configuration change
        if (savedInstanceState == null) {
            StepsListFragment stepsListFragment = StepsListFragment.newInstance(RECIPE_ID);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .add(R.id.fl_detail_list_holder, stepsListFragment)
                    .commit();
        }

    }

    private void setupRecipeDetailView() {
        // hide recipe image in detail only
        detailBinding.incRecipeInfo.ivRecipe.setVisibility(View.INVISIBLE);
        // set listener for special ingredients button
        detailBinding.btnIngredients.setOnClickListener(v -> {
            Logger.d("Open ingredients list adapter here!");
        });
    }

    private void setMainRecipeInfo() {
        // Get repository instance
        DetailViewModelFactory factory = InjectorUtils.provideDetailViewModelFactory(this, RECIPE_ID);
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

    @Override
    public void onDetailListFragmentInteraction(Step step) {
        Logger.d(step.getDescription());
    }
}
