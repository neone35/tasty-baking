package com.example.aarta.tastybaking.data.database;

import android.arch.persistence.room.TypeConverter;

import com.example.aarta.tastybaking.data.models.Step;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class StepConverter {
    private static Gson gson = new Gson();

    @TypeConverter
    public static String objectListToString(List<Step> objects) {
        return gson.toJson(objects);
    }

    @TypeConverter
    public static List<Step> stringToObjectList(String data) {
        if (data == null) {
            return Collections.emptyList();
        }

        Type listType = new TypeToken<List<Step>>() {
        }.getType();

        return gson.fromJson(data, listType);
    }
}
