package com.example.aarta.tastybaking.ui.main;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.example.aarta.tastybaking.data.TastyRepository;
import com.example.aarta.tastybaking.data.models.Recipe;

import java.util.List;

class MainFragViewModel extends ViewModel {

    private final LiveData<List<Recipe>> mRecipes;

    MainFragViewModel(TastyRepository repository) {
        mRecipes = repository.getCurrentRecipes();
    }

    public LiveData<List<Recipe>> getRecipes() {
        return mRecipes;
    }

}