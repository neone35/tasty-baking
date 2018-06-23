package com.example.aarta.tastybaking.ui.detail;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.aarta.tastybaking.R;
import com.example.aarta.tastybaking.data.models.Ingredient;
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
                .inflate(R.layout.steps_detail_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientListItemAdapter.ViewHolder holder, int position) {
        Ingredient oneIngredient = mOneRecipe.getIngredients().get(position);
        String ingredientName = (position + 1) + ". " + oneIngredient.getIngredient();

        holder.mStepDetailsBtn.setText(ingredientName);
    }

    @Override
    public int getItemCount() {
        return mOneRecipe.getIngredients().size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final Button mStepDetailsBtn;

        ViewHolder(View view) {
            super(view);
            mView = view;
            mStepDetailsBtn = view.findViewById(R.id.btn_step_details);
        }
    }
}
