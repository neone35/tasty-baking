package com.example.aarta.tastybaking.ui.detail;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.aarta.tastybaking.R;
import com.example.aarta.tastybaking.data.models.Step;
import com.example.aarta.tastybaking.ui.detail.DetailListFragment.onDetailListFragmentInteractionListener;
import com.example.aarta.tastybaking.data.models.Recipe;


public class StepListItemAdapter extends RecyclerView.Adapter<StepListItemAdapter.ViewHolder> {

    private final Recipe mOneRecipe;
    private final onDetailListFragmentInteractionListener mListener;
    private int selectedPos = -1;

    StepListItemAdapter(Recipe recipe, onDetailListFragmentInteractionListener listener) {
        mOneRecipe = recipe;
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.detail_list_step_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StepListItemAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Step oneStepByPos = mOneRecipe.getSteps().get(position);
        String stepShortDescription = oneStepByPos.getShortDescription();
        if (position != 0)
            stepShortDescription = position + ". " + oneStepByPos.getShortDescription();

        holder.mStepDetailsBtn.setText(stepShortDescription);
        // remove selection if last selected position is not current position
        // add selection if selected position has same position
        holder.mStepDetailsBtn.setSelected(selectedPos == position);
        holder.mStepDetailsBtn.setOnClickListener(v -> {
            if (null != mListener) {
                notifyItemChanged(selectedPos); // remove selection from last item (call bind again)
                selectedPos = position; // set current position
                notifyItemChanged(selectedPos); // add selection to current item (calls bind again)
                // change color to darker when visited
                v.setSelected(true);
                // Notify the active callbacks interface (the activity, if the
                // fragment is attached to one) that an item has been selected.
                mListener.onDetailListFragmentInteraction(mOneRecipe, oneStepByPos);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mOneRecipe.getSteps().size();
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
