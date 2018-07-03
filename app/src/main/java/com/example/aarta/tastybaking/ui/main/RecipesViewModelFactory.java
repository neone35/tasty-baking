package com.example.aarta.tastybaking.ui.main;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.example.aarta.tastybaking.data.TastyRepository;

public class RecipesViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final TastyRepository mRepository;

    public RecipesViewModelFactory(TastyRepository repository) {
        this.mRepository = repository;
    }

    @NonNull
    @Override
    @SuppressWarnings(value = "unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        //noinspection unchecked
        return (T) new RecipesViewModel(mRepository);
    }

}
