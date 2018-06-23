package com.example.aarta.tastybaking.ui.main;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.example.aarta.tastybaking.data.TastyRepository;

public class MainViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final TastyRepository mRepository;

    public MainViewModelFactory(TastyRepository repository) {
        this.mRepository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        //noinspection unchecked
        return (T) new MainViewModel(mRepository);
    }

}
