package com.example.aarta.tastybaking.ui.step;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CircularProgressDrawable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
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
import com.example.aarta.tastybaking.utils.RecipeUtils;
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
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.Objects;

import timber.log.Timber;

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
    private PlayerView mStepExoPlayerView;
    private int mCurrentOrientation;
    private ProgressBar mPbProgressDrawableView;

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

        // show loading indicator
        mPbProgressDrawableView = view.findViewById(R.id.pb_cpd_holder);
        CircularProgressDrawable circularProgressDrawable = RecipeUtils.getCircleProgressDrawable(this.getContext(), 10f, 25f);
        mPbProgressDrawableView.setIndeterminateDrawable(circularProgressDrawable);

        // Get repository instance
        OneRecipeViewModelFactory factory = InjectorUtils.provideDetailViewModelFactory(Objects.requireNonNull(ctx), mRecipeID);
        // Tie fragment & ViewModel together
        OneRecipeViewModel mViewModel = ViewModelProviders.of(this, factory).get(OneRecipeViewModel.class);
        mViewModel.getOneRecipe().observe(this, oneRecipe -> {
            mStepExoPlayerView = view.findViewById(R.id.exo_step_player);
            FrameLayout flDescrHolder = view.findViewById(R.id.sv_description_holder);
            TextView tvShortDescr = view.findViewById(R.id.tv_step_short_description);
            TextView tvLongDescr = view.findViewById(R.id.tv_step_description);
            Button btnPreviousStep = view.findViewById(R.id.btn_previous_step);
            Button btnNextStep = view.findViewById(R.id.btn_next_step);
            if (oneRecipe != null) {
                oneStep = oneRecipe.getSteps().get(mStepID);
                if (oneStep != null) {
                    // setup exoplayer
                    stepExoPlayer = getPreparedExoPlayer(ctx, oneStep.getVideoURL());
                    mStepExoPlayerView.setPlayer(stepExoPlayer);
                    setExoListeners(ctx, stepExoPlayer, flDescrHolder);
                    stepExoPlayer.setPlayWhenReady(false);
                    mStepExoPlayerView.setControllerAutoShow(true);
                    // setup other views
                    mCurrentOrientation = Objects.requireNonNull(getActivity()).getResources().getConfiguration().orientation;
                    if (mCurrentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
                        // change to full screen only on mobile
                        if (Objects.requireNonNull(getActivity()).findViewById(R.id.ll_detail_tablet) == null) {
                            setLandscapeConfig();
                        }
                        // other views for possible change to portrait
                        RecipeUtils.setFormattedDescription(oneStep, tvLongDescr, tvShortDescr);
                    } else if (mCurrentOrientation == Configuration.ORIENTATION_PORTRAIT) {
                        // only texts on new viewmodel bind
                        RecipeUtils.setFormattedDescription(oneStep, tvLongDescr, tvShortDescr);
                    }
                    // enable step switch buttons on mobile only
                    if (Objects.requireNonNull(getActivity()).findViewById(R.id.ll_detail_tablet) == null) {
                        // It's StepActivity (mobile, not DetailActivity)
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

    private void setExoListeners(Context ctx,
                                 SimpleExoPlayer stepExoPlayer,
                                 FrameLayout flDescrHolder) {

        Player.DefaultEventListener defaultEventListener = new Player.DefaultEventListener() {
            int animateSpeed = 500;
            float invisible = 0.0f;
            float visible = 1.0f;

            // loading successful, animate in
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                super.onPlayerStateChanged(playWhenReady, playbackState);
                int maxGonePlayerHeight = 350;
                switch (playbackState) {
                    case Player.STATE_READY:
                        // animate video fade-in
                        mStepExoPlayerView.animate()
                                .alpha(invisible)
                                .translationY(-maxGonePlayerHeight)
                                .setDuration(animateSpeed)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        super.onAnimationEnd(animation);
                                        mStepExoPlayerView.setVisibility(View.VISIBLE);
                                        mStepExoPlayerView.animate()
                                                .alpha(visible)
                                                .translationY(0);
                                        // hide loading indicator
                                        mPbProgressDrawableView.setVisibility(View.GONE);
                                    }
                                });
                        break;
                    default:
                        break;
                }
            }

            // error, hide loading and leave player as GONE
            @Override
            public void onPlayerError(ExoPlaybackException error) {
                Timber.e(error.toString());
                Toast.makeText(ctx, "Video not found", Toast.LENGTH_SHORT).show();
                // hide loading indicator
                mPbProgressDrawableView.setVisibility(View.GONE);
                // if error occurred in landscape, change to portrait config to load description
                if (mCurrentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
                    setPortraitConfig();
                }
            }
        };
        stepExoPlayer.addListener(defaultEventListener);
    }

    private void setLandscapeConfig() {
        // change UI only if player is attached
        if (mStepExoPlayerView.getPlayer().getPlaybackError() == null) {
            // hide action bar
            Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).hide();
            // set exoPlayer to match parent
            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) mStepExoPlayerView.getLayoutParams();
            params.width = ConstraintLayout.LayoutParams.MATCH_PARENT;
            params.height = ConstraintLayout.LayoutParams.MATCH_PARENT;
            params.setMargins(0, 0, 0, 0);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                params.setMarginEnd(0);
                params.setMarginStart(0);
            }
            mStepExoPlayerView.setLayoutParams(params);
        }
    }

    private void setPortraitConfig() {
        // show action bar
        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).show();
        // set exoPlayer dimensions back to initial
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) mStepExoPlayerView.getLayoutParams();
        params.width = ConstraintLayout.LayoutParams.MATCH_PARENT;
        // find out device type to adjust initial height
        float dpRatio = mStepExoPlayerView.getContext().getResources().getDisplayMetrics().density;
        if (Objects.requireNonNull(getActivity()).findViewById(R.id.fl_step_frag_tablet) != null) {
            int TABLET_PLAYER_HEIGHT_PX = 350;
            params.height = (int) (TABLET_PLAYER_HEIGHT_PX * dpRatio);
        } else if (Objects.requireNonNull(getActivity()).findViewById(R.id.fl_step_frag_mobile) != null) {
            int MOBILE_PLAYER_HEIGHT_PX = 200;
            params.height = (int) (MOBILE_PLAYER_HEIGHT_PX * dpRatio);
        }
        // set margins back to initial
        int margin8DP = (int) (8 * dpRatio);
        params.setMargins(margin8DP, margin8DP, margin8DP, margin8DP);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            params.setMarginEnd(margin8DP);
            params.setMarginStart(margin8DP);
        }
        mStepExoPlayerView.setLayoutParams(params);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Checking the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setLandscapeConfig();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            setPortraitConfig();
        }
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
        //* 1. Create a default TrackSelector
        // Measures bandwidth during playback. Can be null if not required.
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        // Selects tracks provided by the MediaSource
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

        //* 2. Prepare the player
        // Produces DataSource instances through which media data is loaded.
        DataSource.Factory dataSourceFactory = null;
        if (ctx != null) {
            String appName = ctx.getApplicationInfo().name;
            dataSourceFactory = new DefaultDataSourceFactory(ctx, Util.getUserAgent(ctx, appName), bandwidthMeter);
        }
        // This is the MediaSource representing the media to be played.
        MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(mp4VideoUri);

        //1+2. Create, prepare player, using results
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
