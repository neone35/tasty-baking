package com.example.aarta.tastybaking.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

import java.util.Objects;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link onRecipeCardsListFragmentInteractionListener}
 * interface.
 */
public class RecipeCardListFragment extends Fragment {

    private static final String COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;
    private onRecipeCardsListFragmentInteractionListener mListener;
    private MainFragViewModel mViewModel;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RecipeCardListFragment() {
    }

    public static RecipeCardListFragment newInstance(int columnCount) {
        RecipeCardListFragment fragment = new RecipeCardListFragment();
        Bundle args = new Bundle();
        args.putInt(COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.addLogAdapter(new AndroidLogAdapter());

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_recipe_card_list, container, false);

        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
        }

        // Get repository instance (erase existing data & observe MutableLiveData trigger)
        MainFragViewModelFactory factory = InjectorUtils.provideMainViewModelFactory(Objects.requireNonNull(this.getContext()));
        // Tie fragment & ViewModel together
        mViewModel = ViewModelProviders.of(this, factory).get(MainFragViewModel.class);
        // Trigger service once & observe change in database
        mViewModel.getRecipes().observe(this, recipes -> {
            if (view instanceof RecyclerView) {
                RecyclerView recyclerView = (RecyclerView) view;
                recyclerView.setAdapter(new RecipeCardItemAdapter(recipes, mListener));
            }
        });

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
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
    public interface onRecipeCardsListFragmentInteractionListener {
        void onListFragmentInteraction(Recipe recipe);
    }
}
