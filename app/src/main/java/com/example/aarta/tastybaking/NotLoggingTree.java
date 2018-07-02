package com.example.aarta.tastybaking;

import android.support.annotation.NonNull;

import timber.log.Timber;

public class NotLoggingTree extends Timber.Tree {
    @Override
    protected void log(final int priority, final String tag, @NonNull final String message, final Throwable throwable) {
    }
}
