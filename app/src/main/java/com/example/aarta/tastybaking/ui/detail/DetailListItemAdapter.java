package com.example.aarta.tastybaking.ui.detail;

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

public class DetailListItemAdapter extends RecyclerView.Adapter<DetailListItemAdapter.ViewHolder> {

    private final Recipe mOneRecipe;
    private final onDetailListFragmentInteractionListener mListener;

    DetailListItemAdapter(Recipe recipe, onDetailListFragmentInteractionListener listener) {
        mOneRecipe = recipe;
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_detail_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DetailListItemAdapter.ViewHolder holder, int position) {
        Step oneStep = mOneRecipe.getSteps().get(position);
        String stepShortDescription = (position + 1) + ". " + oneStep.getShortDescription();

        holder.mStepDetailsBtn.setText(stepShortDescription);
        holder.mStepDetailsBtn.setOnClickListener(v -> {
            if (null != mListener) {
                // Notify the active callbacks interface (the activity, if the
                // fragment is attached to one) that an item has been selected.
                mListener.onDetailListFragmentInteraction(oneStep);
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
