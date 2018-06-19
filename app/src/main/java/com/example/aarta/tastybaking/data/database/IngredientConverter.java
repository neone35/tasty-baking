package com.example.aarta.tastybaking.data.database;

import android.arch.persistence.room.TypeConverter;

import com.example.aarta.tastybaking.data.models.Ingredient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class IngredientConverter {
    private static Gson gson = new Gson();

    @TypeConverter
    public static String objectListToString(List<Ingredient> objects) {
        return gson.toJson(objects);
    }

    @TypeConverter
    public static List<Ingredient> stringToObjectList(String data) {
        if (data == null) {
            return Collections.emptyList();
        }

        Type listType = new TypeToken<List<Ingredient>>() {
        }.getType();

        return gson.fromJson(data, listType);
    }
}
