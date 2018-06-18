package com.example.aarta.tastybaking.ui;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.example.aarta.tastybaking.data.TastyRepository;
import com.example.aarta.tastybaking.data.models.Recipe;

import java.util.List;

class MainViewModel extends ViewModel {

    private final LiveData<List<Recipe>> mRecipes;

    MainViewModel(TastyRepository repository) {
        mRecipes = repository.getCurrentRecipes();
    }

    public LiveData<List<Recipe>> getRecipes() {
        return mRecipes;
    }

}