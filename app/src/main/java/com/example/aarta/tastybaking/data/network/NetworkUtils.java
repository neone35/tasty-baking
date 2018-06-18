package com.example.aarta.tastybaking.data.network;

import com.example.aarta.tastybaking.data.models.Recipe;
import com.orhanobut.logger.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkUtils {

    public static final String RECIPES_URL = "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/";
    public static final String RECIPES_JSON_NAME = "baking.json";

    // Fetch and parse recipes into Recipe model
    public static List<Recipe> getResponseWithUrl(String urlString, String fileName) {
        Call<List<Recipe>> retroCall = getApiService(urlString).getRecipes(fileName);
        List<Recipe> recipeList = new ArrayList<>();
        try {
            recipeList = retroCall.execute().body();
        } catch (IOException e) {
            Logger.e("Exception" + e);
        }
        return recipeList;
    }

    private static Retrofit getRetrofit(String urlString) {
        return new Retrofit.Builder()
                .baseUrl(urlString)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    private static RecipesEndpointInterface getApiService(String urlString) {
        return getRetrofit(urlString).create(RecipesEndpointInterface.class);
    }
}
