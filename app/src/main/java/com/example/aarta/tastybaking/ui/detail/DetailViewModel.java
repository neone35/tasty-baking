package com.example.aarta.tastybaking.ui.detail;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.example.aarta.tastybaking.data.TastyRepository;
import com.example.aarta.tastybaking.data.models.Recipe;

import java.util.List;

public class DetailViewModel extends ViewModel {

    private final LiveData<Recipe> mOneRecipe;

    DetailViewModel(TastyRepository repository, int recipeID) {
        mOneRecipe = repository.getRecipeById(recipeID);
    }

    public LiveData<Recipe> getOneRecipe() {
        return mOneRecipe;
    }
}
