package com.example.aarta.tastybaking.ui.main;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.CircularProgressDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.aarta.tastybaking.GlideApp;
import com.example.aarta.tastybaking.R;
import com.example.aarta.tastybaking.data.models.Recipe;
import com.example.aarta.tastybaking.data.models.Step;
import com.example.aarta.tastybaking.ui.main.RecipesCardListFragment.onRecipeCardsListFragmentInteractionListener;
import com.example.aarta.tastybaking.utils.RecipeUtils;
import com.google.android.exoplayer2.ExoPlayerFactory;
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

import java.util.ArrayList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Recipe} and makes a call to the
 * specified {@link onRecipeCardsListFragmentInteractionListener}.
 */
public class RecipeCardItemAdapter extends RecyclerView.Adapter<RecipeCardItemAdapter.ViewHolder> {

    private final List<Recipe> mRecipeList;
    private final onRecipeCardsListFragmentInteractionListener mListener;
    private Context mContext;
    private SimpleExoPlayer mStepExoPlayer;
    private boolean EXO_RELEASED = true;
    private int mIngredientWidgetID;

    RecipeCardItemAdapter(List<Recipe> recipes, onRecipeCardsListFragmentInteractionListener listener, int ingredientWidgetID) {
        mRecipeList = recipes;
        mListener = listener;
        mIngredientWidgetID = ingredientWidgetID;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.main_list_recipe_card_item, parent, false);
        mContext = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        // bind recipe model objects
        holder.mRecipe = mRecipeList.get(position);
        String recipeName = mRecipeList.get(position).getName();
        String ingredientNum = String.valueOf(mRecipeList.get(position).getIngredients().size());
        String stepsNum = String.valueOf(mRecipeList.get(position).getSteps().size());
        String servingsNum = String.valueOf(mRecipeList.get(position).getServings());

        // find last step video (result is best seen)
        String lastNotEmptyVideoURL = getLastVideoURL(position);
        // load thumbnail into imageView with Glide
        loadVideoThumbnail(mContext, lastNotEmptyVideoURL, holder);

        // check if adapter is attached outside widget config
        // enable exo player & play button listener only when launched normally (not as widget config)
        if (mIngredientWidgetID == AppWidgetManager.INVALID_APPWIDGET_ID || mIngredientWidgetID == -1) {
            // create exoplayer
            mStepExoPlayer = getExoPlayer(mContext, lastNotEmptyVideoURL, holder);
            // listen for exoPlayer thumbnail play button click
            holder.mPlayIconButton.setOnClickListener(v -> {
                // release if new thumbnail clicked without recyclerview scroll before
                if (!EXO_RELEASED) {
                    mStepExoPlayer.release();
                    mStepExoPlayer = getExoPlayer(mContext, lastNotEmptyVideoURL, holder);
                }
                holder.mStepExoPlayerView.setControllerAutoShow(true);
                // tie view & player together
                holder.mStepExoPlayerView.setPlayer(mStepExoPlayer);
                // initialize player with viewHolder mediasource
                mStepExoPlayer.prepare(holder.mMediaSource);
                // play instantly after click
                mStepExoPlayer.setPlayWhenReady(true);
                // provide key for recyclerView to release exoPlayer on scroll
                EXO_RELEASED = false;
                switchToExoPlayerView(holder);
            });
        }

        // setup other views
        holder.mRecipeNameView.setText(recipeName);
        holder.mIngredientNumView.setText(ingredientNum);
        holder.mStepsNumView.setText(stepsNum);
        holder.mServingsNumView.setText(servingsNum);

        // listen for card click to launch detail activity
        holder.mView.setOnClickListener(v -> {
            if (null != mListener) {
                // Notify the active callbacks interface (the activity, if the
                // fragment is attached to one) that an item has been selected.
                mListener.onCardListFragmentInteraction(holder.mRecipe);
            }
        });
    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        super.onViewRecycled(holder);
        switchToThumbnailView(holder);
    }

    private void loadVideoThumbnail(Context ctx, String videoURL, ViewHolder holder) {
        CircularProgressDrawable circularProgressDrawable = RecipeUtils.getCircleProgressDrawable(ctx, 15f, 80f);
        GlideApp.with(ctx)
                .load(videoURL)
                .placeholder(circularProgressDrawable)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {
                        holder.mPlayIconButton.setVisibility(View.VISIBLE);
                        holder.mThumbnailView.setImageDrawable(resource);
                        return true;
                    }
                })
                .into(holder.mThumbnailView);
    }

    private void switchToExoPlayerView(ViewHolder holder) {
        // hide thumbnail
        holder.mThumbnailHolder.setVisibility(View.GONE);
        // show player
        holder.mStepExoPlayerView.setVisibility(View.VISIBLE);
    }

    private void switchToThumbnailView(ViewHolder holder) {
        // hide player
        holder.mStepExoPlayerView.setVisibility(View.GONE);
        // show thumbnail
        holder.mThumbnailHolder.setVisibility(View.VISIBLE);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            // called before adapter.getView (next frame)
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (mStepExoPlayer != null && !EXO_RELEASED) {
                    mStepExoPlayer.release();
                    recyclerView.getAdapter().notifyDataSetChanged();
                    EXO_RELEASED = true;
                }
            }
        });
    }

    private SimpleExoPlayer getExoPlayer(Context ctx, String mp4VideoUriString, ViewHolder holder) {
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
        holder.mMediaSource = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(mp4VideoUri);

        // Create and return player
        return ExoPlayerFactory.newSimpleInstance(ctx, trackSelector);
    }

    private String getLastVideoURL(int position) {
        // find last not empty step video
        List<Step> allSteps = mRecipeList.get(position).getSteps();
        List<String> allNotEmptyVideoURLs = new ArrayList<>();
        for (int i = 0; i < allSteps.size(); i++) {
            String videoURL = allSteps.get(i).getVideoURL();
            if (!videoURL.isEmpty()) {
                allNotEmptyVideoURLs.add(videoURL);
            }
        }
        int videoURLsLength = allNotEmptyVideoURLs.size() - 1;
        return allNotEmptyVideoURLs.get(videoURLsLength);
    }

    @Override
    public int getItemCount() {
        return mRecipeList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final ImageView mThumbnailView;
        final ImageButton mPlayIconButton;
        final FrameLayout mThumbnailHolder;
        final PlayerView mStepExoPlayerView;
        final TextView mRecipeNameView;
        final TextView mIngredientNumView;
        final TextView mStepsNumView;
        final TextView mServingsNumView;
        Recipe mRecipe;
        MediaSource mMediaSource;

        ViewHolder(View view) {
            super(view);
            mView = view;
            mThumbnailView = view.findViewById(R.id.iv_recipe_thumbnail);
            mPlayIconButton = view.findViewById(R.id.ib_recipe_play_icon);
            mThumbnailHolder = view.findViewById(R.id.fl_exo_thumbnail_holder);
            mStepExoPlayerView = view.findViewById(R.id.exo_step_player);
            mRecipeNameView = view.findViewById(R.id.tv_recipe_name);
            mIngredientNumView = view.findViewById(R.id.tv_ingredient_num);
            mStepsNumView = view.findViewById(R.id.tv_steps_num);
            mServingsNumView = view.findViewById(R.id.tv_servings_num);
        }
    }
}
