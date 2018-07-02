package com.example.aarta.tastybaking.data.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import com.example.aarta.tastybaking.data.models.Recipe;

import timber.log.Timber;

// version number needs to be incremented if schema models change
@Database(entities = {Recipe.class}, version = 1)
@TypeConverters({IngredientConverter.class, StepConverter.class})
public abstract class RecipeDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "recipeDB";
    // For Singleton instantiation
    private static final Object LOCK = new Object();
    private static RecipeDatabase sInstance;

    public static RecipeDatabase getInstance(Context context) {
//        Timber.d("Getting the database");
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        RecipeDatabase.class, RecipeDatabase.DATABASE_NAME).build();
                Timber.d("Made new database");
            }
        }
        return sInstance;
    }

    // The associated DAOs for the database
    public abstract RecipeDao recipeDao();
}
