package com.example.aarta.tastybaking.ui;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.aarta.tastybaking.R;
import com.example.aarta.tastybaking.ui.RecipeCardListFragment.onRecipeCardsListFragmentInteractionListener;
import com.example.aarta.tastybaking.data.models.Recipe;
import com.squareup.picasso.Picasso;

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
        String recipeName = mRecipeList.get(position).getName();
        String recipeImageUrl = mRecipeList.get(position).getImage();
        String ingredientNum = String.valueOf(mRecipeList.get(position).getIngredients().size());
        String stepsNum = String.valueOf(mRecipeList.get(position).getSteps().size());
        String servingsNum = String.valueOf(mRecipeList.get(position).getServings());

        if (recipeImageUrl.isEmpty())
            Picasso.get()
                    .load(R.drawable.img_food_placeholder)
                    .placeholder(R.drawable.img_food_placeholder)
                    .fit()
                    .into(holder.mRecipeImageView);
        else
            Picasso.get()
                    .load(recipeImageUrl)
                    .placeholder(R.drawable.img_food_placeholder)
                    .fit()
                    .into(holder.mRecipeImageView);
        holder.mRecipeNameView.setText(recipeName);
        holder.mIngredientNumView.setText(ingredientNum);
        holder.mStepsNumView.setText(stepsNum);
        holder.mServingsNumView.setText(servingsNum);
//        Logger.d(mRecipeList.get(position).getIngredients().get(0).getQuantity());

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

    class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final ImageView mRecipeImageView;
        final TextView mRecipeNameView;
        final TextView mIngredientNumView;
        final TextView mStepsNumView;
        final TextView mServingsNumView;
        Recipe mRecipe;

        ViewHolder(View view) {
            super(view);
            mView = view;
            mRecipeNameView = view.findViewById(R.id.tv_recipe_name);
            mRecipeImageView = view.findViewById(R.id.iv_recipe);
            mIngredientNumView = view.findViewById(R.id.tv_ingredient_num);
            mStepsNumView = view.findViewById(R.id.tv_steps_num);
            mServingsNumView = view.findViewById(R.id.tv_servings_num);
        }

//        @Override
//        public String toString() {
//            return super.toString() + " '" + mRecipeNameView.getText() + "'";
//        }
    }
}
