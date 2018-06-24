package com.example.aarta.tastybaking.ui.step;

import android.databinding.DataBindingUtil;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.aarta.tastybaking.R;
import com.example.aarta.tastybaking.data.models.Recipe;
import com.example.aarta.tastybaking.data.models.Step;
import com.example.aarta.tastybaking.databinding.ActivityStepBinding;
import com.example.aarta.tastybaking.ui.detail.DetailActivity;
import com.example.aarta.tastybaking.ui.main.MainActivity;
import com.orhanobut.logger.Logger;

import java.util.Objects;

public class StepActivity extends AppCompatActivity implements StepFragment.onStepFragmentInteractionListener {

    private static int RECIPE_ID;
    private static int STEP_ID;
    ActivityStepBinding stepBinding;
    private FragmentManager fragmentManager;
    public static final String KEY_PREVIOUS = "previous_click";
    public static final String KEY_NEXT = "next_click";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stepBinding = DataBindingUtil.setContentView(this, R.layout.activity_step);
        fragmentManager = getSupportFragmentManager();

        // check if intent bundle received successfully and setup view
        Bundle mainExtrasBundle = getIntent().getExtras();
        if (mainExtrasBundle != null) {
            RECIPE_ID = mainExtrasBundle.getInt(MainActivity.KEY_SELECTED_RECIPE_ID);
            STEP_ID = mainExtrasBundle.getInt(DetailActivity.KEY_SELECTED_STEP_ID);
            // only create initial fragment if there was no configuration change
            if (savedInstanceState == null) {
                // add initial fragment
                StepFragment stepsListFragment = StepFragment.newInstance(RECIPE_ID, STEP_ID);
                fragmentManager.beginTransaction()
                        .add(R.id.fl_step_holder, stepsListFragment)
                        .commit();
            }
        }
    }

    public void switchStep(int recipeID, int stepID) {
        StepFragment stepsListFragment = StepFragment.newInstance(recipeID, stepID);
        fragmentManager.beginTransaction()
                .replace(R.id.fl_step_holder, stepsListFragment)
                .commit();
    }

    @Override
    public void onStepFragmentInteraction(Recipe currentRecipe, int currentStepID, String whichBtn) {
        int nextStepID = -1;
        int currentRecipeStepsNum = currentRecipe.getSteps().size();
        if (whichBtn.equals(KEY_PREVIOUS)) {
            if (currentStepID > 0)
                nextStepID = currentStepID - 1;
        } else if (whichBtn.equals(KEY_NEXT)) {
            if (currentStepID < currentRecipeStepsNum)
                nextStepID = currentStepID + 1;
        }

        int currentRecipeID = currentRecipe.getId();
        if (nextStepID != -1)
            switchStep(currentRecipeID, nextStepID);
        Logger.d("current step: ", currentStepID);
        Logger.d("next step: ", nextStepID);
    }
}
