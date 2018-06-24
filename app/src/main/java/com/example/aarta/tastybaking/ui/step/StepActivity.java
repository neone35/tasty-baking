package com.example.aarta.tastybaking.ui.step;

import android.databinding.DataBindingUtil;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.aarta.tastybaking.R;
import com.example.aarta.tastybaking.databinding.ActivityStepBinding;
import com.example.aarta.tastybaking.ui.detail.DetailActivity;
import com.example.aarta.tastybaking.ui.main.MainActivity;

public class StepActivity extends AppCompatActivity {

    private static int RECIPE_ID;
    private static int STEP_ID;
    ActivityStepBinding stepBinding;
    private FragmentManager fragmentManager;

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
                // TODO: switch to another step on click
            }
        }
    }
}
