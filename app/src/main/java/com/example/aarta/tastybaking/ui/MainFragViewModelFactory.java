package com.example.aarta.tastybaking.ui;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.example.aarta.tastybaking.data.TastyRepository;

public class MainFragViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final TastyRepository mRepository;

    public MainFragViewModelFactory(TastyRepository repository) {
        this.mRepository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        //noinspection unchecked
        return (T) new MainFragViewModel(mRepository);
    }

}
