package com.example.aarta.tastybaking.ui;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.aarta.tastybaking.R;
import com.example.aarta.tastybaking.ui.RecipeCardListFragment.onRecipeCardsListFragmentInteractionListener;
import com.example.aarta.tastybaking.data.models.Recipe;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Recipe} and makes a call to the
 * specified {@link onRecipeCardsListFragmentInteractionListener}.
 */
public class RecipeCardItemAdapter extends RecyclerView.Adapter<RecipeCardItemAdapter.ViewHolder> {

    private final List<Recipe> mRecipeList;
    private final onRecipeCardsListFragmentInteractionListener mListener;

    RecipeCardItemAdapter(List<Recipe> recipes, onRecipeCardsListFragmentInteractionListener listener) {
        mRecipeList = recipes;
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_recipe_card_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.mRecipe = mRecipeList.get(position);
        holder.mIdView.setText(String.valueOf(mRecipeList.get(position).getId()));
        holder.mContentView.setText(mRecipeList.get(position).getName());

        holder.mView.setOnClickListener(v -> {
            if (null != mListener) {
                // Notify the active callbacks interface (the activity, if the
                // fragment is attached to one) that an item has been selected.
                mListener.onListFragmentInteraction(holder.mRecipe);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mRecipeList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final TextView mIdView;
        final TextView mContentView;
        public Recipe mRecipe;

        ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = view.findViewById(R.id.item_number);
            mContentView = view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
