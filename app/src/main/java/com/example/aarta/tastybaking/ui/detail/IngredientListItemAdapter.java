package com.example.aarta.tastybaking.ui.detail;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.aarta.tastybaking.R;
import com.example.aarta.tastybaking.data.models.Recipe;


public class IngredientListItemAdapter extends RecyclerView.Adapter<IngredientListItemAdapter.ViewHolder> {
    private final Recipe mOneRecipe;

    IngredientListItemAdapter(Recipe recipe) {
        mOneRecipe = recipe;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.detail_ingredient_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientListItemAdapter.ViewHolder holder, int position) {
        String ingredientName = mOneRecipe.getIngredients().get(position).getIngredient();
        String ingredientNameCaps = ingredientName.substring(0, 1).toUpperCase() + ingredientName.substring(1);
        String ingredientNameWithNumbering = (position + 1) + ". " + ingredientNameCaps;

        String quantity = String.valueOf(mOneRecipe.getIngredients().get(position).getQuantity());
        String measure = mOneRecipe.getIngredients().get(position).getMeasure();

        holder.mIngredientName.setText(ingredientNameWithNumbering);
        holder.mQuantity.setText(quantity);
        holder.mMeasure.setText(measure);
    }

    @Override
    public int getItemCount() {
        return mOneRecipe.getIngredients().size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final TextView mIngredientName;
        final TextView mQuantity;
        final TextView mMeasure;

        ViewHolder(View view) {
            super(view);
            mView = view;
            mIngredientName = view.findViewById(R.id.tv_ingredient_name);
            mQuantity = view.findViewById(R.id.tv_quantity);
            mMeasure = view.findViewById(R.id.tv_measure);
        }
    }
}
