package com.example.aarta.tastybaking.ui.step;


import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aarta.tastybaking.R;
import com.example.aarta.tastybaking.data.models.Recipe;
import com.example.aarta.tastybaking.data.models.Step;
import com.example.aarta.tastybaking.ui.detail.DetailActivity;
import com.example.aarta.tastybaking.ui.detail.DetailListFragment;
import com.example.aarta.tastybaking.ui.detail.DetailViewModel;
import com.example.aarta.tastybaking.ui.detail.DetailViewModelFactory;
import com.example.aarta.tastybaking.ui.main.MainActivity;
import com.example.aarta.tastybaking.utils.InjectorUtils;
import com.orhanobut.logger.Logger;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StepFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StepFragment extends Fragment {

    private int mRecipeID = 0;
    private int mStepID = 0;
    private Step oneStep = null;
    private onStepFragmentInteractionListener mListener;

    public StepFragment() {
        // Required empty public constructor
    }

    public static StepFragment newInstance(int recipeID, int stepID) {
        StepFragment fragment = new StepFragment();
        Bundle args = new Bundle();
        args.putInt(MainActivity.KEY_SELECTED_RECIPE_ID, recipeID);
        args.putInt(DetailActivity.KEY_SELECTED_STEP_ID, stepID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mRecipeID = getArguments().getInt(MainActivity.KEY_SELECTED_RECIPE_ID);
            mStepID = getArguments().getInt(DetailActivity.KEY_SELECTED_STEP_ID);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_step, container, false);

        // Get repository instance
        DetailViewModelFactory factory = InjectorUtils.provideDetailViewModelFactory(Objects.requireNonNull(this.getContext()), mRecipeID);
        // Tie fragment & ViewModel together
        DetailViewModel mViewModel = ViewModelProviders.of(this, factory).get(DetailViewModel.class);
        mViewModel.getOneRecipe().observe(this, oneRecipe -> {
            TextView tvVideoUrl = view.findViewById(R.id.step_media_player);
            TextView tvShortDescr = view.findViewById(R.id.tv_step_short_description);
            TextView tvLongDescr = view.findViewById(R.id.tv_step_description);
            Button btnPreviousStep = view.findViewById(R.id.btn_previous_step);
            Button btnNextStep = view.findViewById(R.id.btn_next_step);
            if (oneRecipe != null) {
                oneStep = oneRecipe.getSteps().get(mStepID);
                if (oneStep != null) {
                    tvVideoUrl.setText(oneStep.getVideoURL());
                    tvShortDescr.setText(oneStep.getShortDescription());
                    tvLongDescr.setText(oneStep.getDescription());

                    btnPreviousStep.setOnClickListener(v -> {
                        if (null != mListener) {
                            mListener.onStepFragmentInteraction(oneRecipe, mStepID, StepActivity.KEY_PREVIOUS);
                        }
                    });
                    btnNextStep.setOnClickListener(v -> {
                        if (null != mListener) {
                            mListener.onStepFragmentInteraction(oneRecipe, mStepID, StepActivity.KEY_NEXT);
                        }
                    });
                } else {
                    Toast.makeText(this.getContext(), "Recipe step not found", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof onStepFragmentInteractionListener) {
            mListener = (onStepFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement onStepFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface onStepFragmentInteractionListener {
        void onStepFragmentInteraction(Recipe currentRecipe, int currentStepID, String whichBtn);
    }

}
