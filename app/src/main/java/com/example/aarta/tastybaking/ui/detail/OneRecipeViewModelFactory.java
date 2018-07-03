package com.example.aarta.tastybaking.ui.detail;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.example.aarta.tastybaking.data.TastyRepository;

public class OneRecipeViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final TastyRepository mRepository;
    private final int mRecipeID;

    public OneRecipeViewModelFactory(TastyRepository repository, int recipeID) {
        this.mRepository = repository;
        this.mRecipeID = recipeID;
    }

    @NonNull
    @Override
    @SuppressWarnings(value = "unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new OneRecipeViewModel(mRepository, mRecipeID);
    }

}
