package com.example.aarta.tastybaking.ui.main;

import android.appwidget.AppWidgetManager;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.aarta.tastybaking.R;
import com.example.aarta.tastybaking.data.models.Recipe;
import com.example.aarta.tastybaking.utils.InjectorUtils;

import java.util.Objects;

import timber.log.Timber;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link onRecipeCardsListFragmentInteractionListener}
 * interface.
 */
public class RecipesCardListFragment extends Fragment {

    private onRecipeCardsListFragmentInteractionListener mListener;
    private Parcelable recyclerViewState;
    private RecyclerView mRecyclerView;
    private int mIngredientWidgetID = -1;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RecipesCardListFragment() {

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
        final View view = inflater.inflate(R.layout.main_list, container, false);

        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            mRecyclerView = (RecyclerView) view;
            int tabletFragmentID = R.id.cards_list_holder_tablet;
            // set linear (mobile) or grid (tablet) layout manager
            if (getFragmentManager() != null) {
                if (getFragmentManager().findFragmentById(tabletFragmentID) == null) {
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
                } else {
                    int columnCount = 3;
                    mRecyclerView.setLayoutManager(new GridLayoutManager(context, columnCount));
                }
            }
        }

        // Get repository instance (start observing MutableLiveData trigger)
        RecipesViewModelFactory factory = InjectorUtils.provideMainViewModelFactory(Objects.requireNonNull(this.getContext()));
        // Tie fragment & ViewModel together
        RecipesViewModel mViewModel = ViewModelProviders.of(this, factory).get(RecipesViewModel.class);
        // Trigger LiveData notification on fragment creation & observe change in DB calling DAO
        mViewModel.getRecipes().observe(this, recipes -> {
            if (view instanceof RecyclerView) {
                Timber.d("Setting card list adapter");
                // send out recipes, click listener and widget ID (if launched as config activity)
                mRecyclerView.setAdapter(new RecipeCardItemAdapter(recipes, mListener, mIngredientWidgetID));
            }
        });

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // check if fragment attached while activity launched as widget config
        if (getActivity() != null) {
            // set widget id which launched main activity
            mIngredientWidgetID = getActivity().getIntent().getIntExtra(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // attach listener to this fragment
        if (context instanceof onRecipeCardsListFragmentInteractionListener) {
            mListener = (onRecipeCardsListFragmentInteractionListener) context;
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    // forward listener from adapter into main activity
    public interface onRecipeCardsListFragmentInteractionListener {
        void onCardListFragmentInteraction(Recipe recipe);
    }
}
