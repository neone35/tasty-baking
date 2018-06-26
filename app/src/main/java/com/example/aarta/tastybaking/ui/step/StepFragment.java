package com.example.aarta.tastybaking.ui.step;


import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.net.Uri;
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
import com.example.aarta.tastybaking.ui.detail.OneRecipeViewModel;
import com.example.aarta.tastybaking.ui.detail.OneRecipeViewModelFactory;
import com.example.aarta.tastybaking.ui.main.MainActivity;
import com.example.aarta.tastybaking.utils.InjectorUtils;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
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
    private SimpleExoPlayer stepExoPlayer;

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
        Context ctx = this.getContext();

        // Get repository instance
        OneRecipeViewModelFactory factory = InjectorUtils.provideDetailViewModelFactory(Objects.requireNonNull(ctx), mRecipeID);
        // Tie fragment & ViewModel together
        OneRecipeViewModel mViewModel = ViewModelProviders.of(this, factory).get(OneRecipeViewModel.class);
        mViewModel.getOneRecipe().observe(this, oneRecipe -> {
            PlayerView stepExoPlayerView = view.findViewById(R.id.exo_step_player);
            TextView tvShortDescr = view.findViewById(R.id.tv_step_short_description);
            TextView tvLongDescr = view.findViewById(R.id.tv_step_description);
            Button btnPreviousStep = view.findViewById(R.id.btn_previous_step);
            Button btnNextStep = view.findViewById(R.id.btn_next_step);
            if (oneRecipe != null) {
                oneStep = oneRecipe.getSteps().get(mStepID);
                if (oneStep != null) {
                    // setup exoplayer
                    stepExoPlayer = getPreparedExoPlayer(ctx, oneStep.getVideoURL());
                    setupExoPlayer(ctx, stepExoPlayer, stepExoPlayerView);
                    stepExoPlayer.setPlayWhenReady(false);
                    stepExoPlayerView.setControllerAutoShow(true);

                    tvShortDescr.setText(oneStep.getShortDescription());
                    tvLongDescr.setText(oneStep.getDescription());
//                    Logger.d(getActivity());
                    // enable step switch buttons on mobile only
                    if (Objects.requireNonNull(getActivity()).findViewById(R.id.ll_detail_tablet) == null) {
                        // It's StepActivity (mobile)
                        // on tablet buttons are GONE, so no need to set up listeners
                        setBtnListeners(btnPreviousStep, btnNextStep, oneRecipe);
                        // hide next / prev button if step doesn't exist
                        controlBtnVisibility(oneRecipe, view);
                    }
                } else {
                    Toast.makeText(ctx, "Recipe step not found", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(ctx, "Recipe not found", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void setupExoPlayer(Context ctx, SimpleExoPlayer stepExoPlayer, PlayerView stepExoPlayerView) {
        stepExoPlayerView.setPlayer(stepExoPlayer);
        Player.DefaultEventListener defaultEventListener = new Player.DefaultEventListener() {
            @Override
            public void onPlayerError(ExoPlaybackException error) {
                Logger.e(error.toString());
                Toast.makeText(ctx, "Video not found", Toast.LENGTH_SHORT).show();
                stepExoPlayerView.setVisibility(View.GONE);
            }
        };
        stepExoPlayer.addListener(defaultEventListener);
    }

    private void controlBtnVisibility(Recipe oneRecipe, View view) {
        int firstStepID = oneRecipe.getSteps().get(0).getId();
        int lastStepID = oneRecipe.getSteps().size() - 1;
        if (mStepID == lastStepID) {
            view.findViewById(R.id.btn_next_step).setVisibility(View.INVISIBLE);
        } else if (mStepID == firstStepID) {
            view.findViewById(R.id.btn_previous_step).setVisibility(View.INVISIBLE);
        }
    }

    private void setBtnListeners(Button btnPreviousStep, Button btnNextStep, Recipe oneRecipe) {
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
    }

    private SimpleExoPlayer getPreparedExoPlayer(Context ctx, String mp4VideoUriString) {
        Uri mp4VideoUri = Uri.parse(mp4VideoUriString);
        // 1. Create a default TrackSelector
        // Measures bandwidth during playback. Can be null if not required.
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        // Selects tracks provided by the MediaSource
        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector =
                new DefaultTrackSelector(videoTrackSelectionFactory);

        // 2. Prepare the player
        // Produces DataSource instances through which media data is loaded.
        DataSource.Factory dataSourceFactory = null;
        if (ctx != null) {
            dataSourceFactory = new DefaultDataSourceFactory(ctx,
                    Util.getUserAgent(ctx, ctx.getApplicationInfo().name), bandwidthMeter);
        }
        // This is the MediaSource representing the media to be played.
        MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(mp4VideoUri);

        // 3. Create, prepare player, using results from (1) and (2)
        SimpleExoPlayer exoPlayer = ExoPlayerFactory.newSimpleInstance(ctx, trackSelector);
        exoPlayer.prepare(videoSource);

        return exoPlayer;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof onStepFragmentInteractionListener) {
            mListener = (onStepFragmentInteractionListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        stepExoPlayer.release();
    }

    public interface onStepFragmentInteractionListener {
        void onStepFragmentInteraction(Recipe currentRecipe, int currentStepID, String whichBtn);
    }

}
