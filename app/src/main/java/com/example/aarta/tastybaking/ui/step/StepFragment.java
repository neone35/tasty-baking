package com.example.aarta.tastybaking.ui.step;


import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.CircularProgressDrawable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aarta.tastybaking.GlideApp;
import com.example.aarta.tastybaking.R;
import com.example.aarta.tastybaking.data.models.Recipe;
import com.example.aarta.tastybaking.data.models.Step;
import com.example.aarta.tastybaking.databinding.FragmentStepBinding;
import com.example.aarta.tastybaking.ui.detail.DetailActivity;
import com.example.aarta.tastybaking.ui.detail.OneRecipeViewModel;
import com.example.aarta.tastybaking.ui.detail.OneRecipeViewModelFactory;
import com.example.aarta.tastybaking.ui.main.MainActivity;
import com.example.aarta.tastybaking.ui.main.RecipeCardItemAdapter;
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
    private SimpleExoPlayer mStepExoPlayer;
    private PlayerView mStepExoPlayerView;
    private int mCurrentOrientation;
    private ProgressBar mPbProgressDrawableView;
    private FragmentStepBinding stepBinding;
    private static final String KEY_PLAYER_POSITION = "player_position";
    private static final String KEY_IS_PLAYER_PLAYING = "is_player_playing";
    private FrameLayout mMasterExoHolder;

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
        stepBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_step, container, false);
        Context ctx = this.getContext();
        mCurrentOrientation = Objects.requireNonNull(getActivity()).getResources().getConfiguration().orientation;

        // create && show loading indicator
        mPbProgressDrawableView = stepBinding.pbCpdHolder;
        CircularProgressDrawable circularProgressDrawable = RecipeUtils.getCircleProgressDrawable(this.getContext(), 10f, 25f);
        mPbProgressDrawableView.setIndeterminateDrawable(circularProgressDrawable);

        // Get repository instance
        OneRecipeViewModelFactory factory = InjectorUtils.provideDetailViewModelFactory(Objects.requireNonNull(ctx), mRecipeID);
        // Tie fragment & ViewModel together
        OneRecipeViewModel mViewModel = ViewModelProviders.of(this, factory).get(OneRecipeViewModel.class);
        mViewModel.getOneRecipe().observe(this, oneRecipe -> {
            // find views
            mMasterExoHolder = stepBinding.incExoHolder.flExoHolderInclude;
            ImageView ivThumbnailHolder = stepBinding.incExoHolder.ivRecipeThumbnail;
            mStepExoPlayerView = stepBinding.incExoHolder.exoStepPlayer;
            TextView tvShortDescr = stepBinding.tvStepShortDescription;
            TextView tvLongDescr = stepBinding.tvStepDescription;
            Button btnPreviousStep = stepBinding.btnPreviousStep;
            Button btnNextStep = stepBinding.btnNextStep;
            ImageButton exoPlayButton = stepBinding.incExoHolder.ibRecipePlayIcon;
            // assign views
            if (oneRecipe != null) {
                oneStep = oneRecipe.getSteps().get(mStepID);
                if (oneStep != null) {
                    String thumbnailURL = oneStep.getThumbnailURL();
                    String videoURL = oneStep.getVideoURL();
                    boolean thumbnailLoaded = loadVideoThumbnail(ctx, thumbnailURL, videoURL, ivThumbnailHolder, exoPlayButton);
                    // if thumbnail loaded and there was no config change,
                    // show play button and listen for its click
                    if (savedInstanceState == null && thumbnailLoaded) {
                        if (!videoURL.isEmpty()) {
                            // switch to fullscreen on mobile (It's not DetailActivity)
                            switchToFullScreenOnMobileInLandscape(true);
                            // assign mMediaSource at videoURL to exoPlayer trackSelector
                            mStepExoPlayer = getPreparedExoPlayer(ctx, oneStep.getVideoURL());
                            // player is playing as soon as it's ready or hidden on error
                            setExoListeners(ctx, mStepExoPlayer);
                            // play on click
                            exoPlayButton.setOnClickListener(v -> {
                                mStepExoPlayerView.setPlayer(mStepExoPlayer);
                                mStepExoPlayerView.setVisibility(View.VISIBLE);
                                mStepExoPlayer.setPlayWhenReady(true);
                                mStepExoPlayerView.setControllerAutoShow(true);
                            });
                        } else {
                            // hide main exo holder and progress bar if videoURL is empty
                            mMasterExoHolder.setVisibility(View.GONE);
                            mPbProgressDrawableView.setVisibility(View.GONE);
                            // show message
                            Toast.makeText(ctx, "Video not found", Toast.LENGTH_SHORT).show();
                            // switch to portrait (cancel fullscreen)
                            switchToFullScreenOnMobileInLandscape(false);
                        }
                    }
                    // setup other views
                    RecipeUtils.setFormattedDescription(oneStep, tvLongDescr, tvShortDescr);
                    // enable step switch buttons on mobile only
                    setupStepSwitchButtonsOnMobile(oneRecipe, btnPreviousStep, btnNextStep);
                } else {
                    Toast.makeText(ctx, "Recipe step not found", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(ctx, "Recipe not found", Toast.LENGTH_SHORT).show();
            }
        });

        return stepBinding.getRoot();
    }

    private void switchToFullScreenOnMobileInLandscape(boolean switchFullscreen) {
        if (mCurrentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (getActivity() instanceof StepActivity) {
                if (switchFullscreen) {
                    setLandscapeConfig();
                } else {
                    setPortraitConfig();
                }
            }
        }
    }

    // save and restore exoPlayer position and play/pause state
    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null && mStepExoPlayer != null) {
            long savedPlayerPos = savedInstanceState.getLong(KEY_PLAYER_POSITION);
            boolean isPlayerPlaying = savedInstanceState.getBoolean(KEY_IS_PLAYER_PLAYING);
            Timber.d("Saved player position is %s", savedPlayerPos);
            Timber.d("Is player playing? %s", isPlayerPlaying);
            mStepExoPlayer.seekTo(savedPlayerPos);
            mStepExoPlayer.setPlayWhenReady(isPlayerPlaying);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mStepExoPlayer != null) {
            long currentPlayerPos = mStepExoPlayer.getCurrentPosition();
            boolean isPlayerPlaying = mStepExoPlayer.getPlayWhenReady();
            outState.putLong(KEY_PLAYER_POSITION, currentPlayerPos);
            outState.putBoolean(KEY_IS_PLAYER_PLAYING, isPlayerPlaying);
        }
    }

    private void setupStepSwitchButtonsOnMobile(Recipe oneRecipe, Button btnPreviousStep, Button btnNextStep) {
        // It's StepActivity (mobile, not DetailActivity)
        // on tablet buttons are GONE, so no need to do this
        if (getActivity() instanceof StepActivity) {
            // set button listeners
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
            // control button visibility
            int firstStepID = oneRecipe.getSteps().get(0).getId();
            int lastStepID = oneRecipe.getSteps().size() - 1;
            if (mStepID == lastStepID) {
                btnNextStep.setVisibility(View.INVISIBLE);
            } else if (mStepID == firstStepID) {
                btnPreviousStep.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void setExoListeners(Context ctx, SimpleExoPlayer stepExoPlayer) {

        Player.DefaultEventListener defaultEventListener = new Player.DefaultEventListener() {

            // loading successful, show player
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                super.onPlayerStateChanged(playWhenReady, playbackState);
                if (playbackState == Player.STATE_READY) {
                    switchToFullScreenOnMobileInLandscape(true);
                    // show exoPlayer and hide loading indicator
                    Timber.d("Playback state is %s", playbackState);
                    mPbProgressDrawableView.setVisibility(View.GONE);
                }
            }

            // error loading video
            @Override
            public void onPlayerError(ExoPlaybackException error) {
                Timber.e(error.toString());
                // hide master exo holder and loading indicator
                mPbProgressDrawableView.setVisibility(View.GONE);
                mMasterExoHolder.setVisibility(View.GONE);
                // if error occurred in landscape, change to portrait config to exit fullscreen
                switchToFullScreenOnMobileInLandscape(false);
                Toast.makeText(ctx, "Video not found", Toast.LENGTH_SHORT).show();
            }
        };
        stepExoPlayer.addListener(defaultEventListener);
    }

    private boolean loadVideoThumbnail(Context ctx, String thumbnailURL, String videoURL,
                                       ImageView ivThumbnailHolder, ImageButton playButton) {
        CircularProgressDrawable circularProgressDrawable = RecipeUtils.getCircleProgressDrawable(ctx, 15f, 80f);
        Drawable thumbnailDrawable = ContextCompat.getDrawable(ctx, R.drawable.img_food_placeholder);
        // 1st load thumbnail from provided JSON URL
        if (!thumbnailURL.isEmpty()) {
            GlideApp.with(ctx)
                    .load(thumbnailURL)
                    .placeholder(circularProgressDrawable)
                    .into(ivThumbnailHolder);
            playButton.setVisibility(View.VISIBLE);
            return true;
        } else {
            if (!videoURL.isEmpty()) {
                // 2nd load thumbnail straight from mp4 with glide
                RecipeCardItemAdapter.loadVideoThumbnail(ctx, videoURL, playButton, ivThumbnailHolder);
                return true;
            } else {
                // 3rd load local drawable
                GlideApp.with(ctx)
                        .load(thumbnailDrawable)
                        .placeholder(circularProgressDrawable)
                        .into(ivThumbnailHolder);
                playButton.setVisibility(View.VISIBLE);
                return true;
            }
        }
    }


    private void setLandscapeConfig() {
        // hide action bar
        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).hide();
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) mMasterExoHolder.getLayoutParams();

        // find out device type to adjust initial params back
        if (getActivity() instanceof DetailActivity) {
            // TABLET
            // set exoPlayer dimensions back to initial
            params.width = ConstraintLayout.LayoutParams.MATCH_PARENT;
            Resources res = mMasterExoHolder.getContext().getResources();
            int tabletPlayerHeight = (int) res.getDimension(R.dimen.exo_player_height_tablet);
            int tabletPlayerMargin = (int) res.getDimension(R.dimen.exo_player_margin_tablet);
            setInitialPlayerParams(params, tabletPlayerHeight, tabletPlayerMargin);
        } else if (getActivity() instanceof StepActivity) {
            // MOBILE
            // set exoPlayer fullscreen
            params.width = ConstraintLayout.LayoutParams.MATCH_PARENT;
            params.height = ConstraintLayout.LayoutParams.MATCH_PARENT;
            params.setMargins(0, 0, 0, 0);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                params.setMarginEnd(0);
                params.setMarginStart(0);
            }
        }
        mMasterExoHolder.setLayoutParams(params);
    }

    private void setPortraitConfig() {
        // show action bar
        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).show();
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) mMasterExoHolder.getLayoutParams();
        params.width = ConstraintLayout.LayoutParams.MATCH_PARENT;

        // set exoPlayer dimensions back to initial
        Resources res = mMasterExoHolder.getContext().getResources();
        int tabletPlayerMargin = (int) res.getDimension(R.dimen.exo_player_margin_tablet);
        int mobilePlayerHeight = (int) res.getDimension(R.dimen.exo_player_height);
        int mobilePlayerMargin = (int) res.getDimension(R.dimen.exo_player_margin);
        // find out device type to adjust initial params back
        if (getActivity() instanceof DetailActivity) {
            // TABLET
            setInitialPlayerParams(params, mobilePlayerHeight, tabletPlayerMargin);
        } else if (getActivity() instanceof StepActivity) {
            // MOBILE
            setInitialPlayerParams(params, mobilePlayerHeight, mobilePlayerMargin);
        }
        mMasterExoHolder.setLayoutParams(params);
    }

    private void setInitialPlayerParams(ConstraintLayout.LayoutParams params, int height, int margin) {
        params.height = height;
        params.setMargins(margin, margin, margin, margin);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            params.setMarginEnd(margin);
            params.setMarginStart(margin);
        }
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

    private SimpleExoPlayer getPreparedExoPlayer(Context ctx, String mp4VideoUriString) {
        Uri mp4VideoUri = Uri.parse(mp4VideoUriString);
        // 1. Create a default TrackSelector
        // Measures bandwidth during playback. Can be null if not required.
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        // Selects tracks provided by the MediaSource
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

        // 2. Prepare the player
        // Produces DataSource instances through which media data is loaded.
        DataSource.Factory dataSourceFactory = null;
        if (ctx != null) {
            String appName = ctx.getApplicationInfo().name;
            dataSourceFactory = new DefaultDataSourceFactory(ctx, Util.getUserAgent(ctx, appName), bandwidthMeter);
        }
        // This is the MediaSource representing the media to be played.
        MediaSource mediaSource = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(mp4VideoUri);

        SimpleExoPlayer simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(ctx, trackSelector);
        simpleExoPlayer.prepare(mediaSource);

        // Create and return player
        return simpleExoPlayer;
    }

    // not in foreground
    @Override
    public void onPause() {
        super.onPause();
        Timber.d("onPause is called");
        // onStop is only called by system, so release earlier
        if (Util.SDK_INT <= 23 && mStepExoPlayer != null) {
            mStepExoPlayer.release();
            Timber.d("Player has been released");
        }
    }

    // no longer visible
    @Override
    public void onStop() {
        super.onStop();
        // newer devices support Multi-Window and before it call onPause
        // we want to keep player in this mode in onPause
        Timber.d("onStop is called");
        if (Util.SDK_INT > 23 && mStepExoPlayer != null) {
            mStepExoPlayer.release();
            Timber.d("Player has been released");
        }
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
//        mStepExoPlayer.release();
    }

    public interface onStepFragmentInteractionListener {
        void onStepFragmentInteraction(Recipe currentRecipe, int currentStepID, String whichBtn);
    }

}
