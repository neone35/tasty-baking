package com.example.aarta.tastybaking.ui.step;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.example.aarta.tastybaking.R;
import com.example.aarta.tastybaking.data.models.Recipe;
import com.example.aarta.tastybaking.databinding.ActivityStepBinding;
import com.example.aarta.tastybaking.ui.detail.DetailActivity;
import com.example.aarta.tastybaking.ui.main.MainActivity;

import timber.log.Timber;

// used on mobile only
public class StepActivity extends AppCompatActivity implements StepFragment.onStepFragmentInteractionListener {

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
            int RECIPE_ID = mainExtrasBundle.getInt(MainActivity.KEY_SELECTED_RECIPE_ID);
            int STEP_ID = mainExtrasBundle.getInt(DetailActivity.KEY_SELECTED_STEP_ID);
            // only create initial fragment if there was no configuration change
            if (savedInstanceState == null) {
                // add initial fragment
                StepFragment stepsListFragment = StepFragment.newInstance(RECIPE_ID, STEP_ID);
                fragmentManager.beginTransaction()
                        .add(R.id.frag_step, stepsListFragment)
                        .commit();
                setupActionBar(getResources().getString(R.string.step_details));
            }
        }
    }

    public void switchStep(int recipeID, int stepID) {
        StepFragment stepsListFragment = StepFragment.newInstance(recipeID, stepID);
        fragmentManager.beginTransaction()
                .replace(R.id.frag_step, stepsListFragment)
                .commit();

    }

    // prev / next step button controller
    @Override
    public void onStepFragmentInteraction(Recipe currentRecipe, int currentStepID, String whichBtn) {
        int nextStepID = -1;
        // step ids begin at 0
        int currentRecipeStepsNum = currentRecipe.getSteps().size() - 1;
        // calc next step ID depending on button click
        if (whichBtn.equals(KEY_PREVIOUS)) {
            if (currentStepID > 0)
                nextStepID = currentStepID - 1;
        } else if (whichBtn.equals(KEY_NEXT)) {
            if (currentStepID < currentRecipeStepsNum)
                nextStepID = currentStepID + 1;
        }

        Timber.d("Next step ID%s", nextStepID);

        int currentRecipeID = currentRecipe.getId();
        // if next step exists, switch step
        if (nextStepID != -1)
            switchStep(currentRecipeID, nextStepID);
    }

    public void setupActionBar(String heading) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(heading);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
}
