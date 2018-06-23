package com.example.aarta.tastybaking.ui.detail;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.aarta.tastybaking.R;
import com.example.aarta.tastybaking.data.models.Step;
import com.example.aarta.tastybaking.ui.main.MainActivity;
import com.example.aarta.tastybaking.utils.InjectorUtils;
import com.orhanobut.logger.Logger;

import java.util.Objects;

/**
 * A placeholder fragment containing a simple view.
 */
public class StepsListFragment extends Fragment {

    private int mRecipeID = 0;
    private Parcelable recyclerViewState;
    private RecyclerView mRecyclerView;
    private onDetailListFragmentInteractionListener mListener;

    public StepsListFragment() {
    }

    public static StepsListFragment newInstance(int recipeID) {
        StepsListFragment fragment = new StepsListFragment();
        Bundle args = new Bundle();
        args.putInt(MainActivity.KEY_SELECTED_RECIPE_ID, recipeID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mRecipeID = getArguments().getInt(MainActivity.KEY_SELECTED_RECIPE_ID);
        }
    }

    // storing and restoring recyclerview scroll position
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        recyclerViewState = mRecyclerView.getLayoutManager().onSaveInstanceState();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRecyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.steps_detail_list, container, false);

        // find recyclerView & set observer at one recipe
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            mRecyclerView = (RecyclerView) view;
            mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        }
        // Get repository instance
        DetailViewModelFactory factory = InjectorUtils.provideDetailViewModelFactory(Objects.requireNonNull(this.getContext()), mRecipeID);
        // Tie fragment & ViewModel together
        DetailViewModel mViewModel = ViewModelProviders.of(this, factory).get(DetailViewModel.class);
        // Observer also gets notified when lifecycle owner switches from inactive to active
        // And receives data only if it has changed
        mViewModel.getOneRecipe().observe(this, oneRecipe -> {
            if (view instanceof RecyclerView) {
                Logger.d("Setting step adapter");
                mRecyclerView.setAdapter(new StepsListItemAdapter(oneRecipe, mListener));
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof onDetailListFragmentInteractionListener) {
            mListener = (onDetailListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface onDetailListFragmentInteractionListener {
        void onDetailListFragmentInteraction(Step step);
    }
}
