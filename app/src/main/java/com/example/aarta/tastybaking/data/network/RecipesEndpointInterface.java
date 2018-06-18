package com.example.aarta.tastybaking.data.network;

import com.example.aarta.tastybaking.data.models.Recipe;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface RecipesEndpointInterface {

    @GET("{fileName}")
    Call<List<Recipe>> getRecipes(@Path("fileName") String jsonFileName);

}
