package com.johnreg.recipeapp.data.remote

import com.johnreg.recipeapp.data.models.Joke
import com.johnreg.recipeapp.data.models.Recipe
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface RecipeApi {

    /*
    @GET - here we have specified the exact endpoint to request and receive data from our api
    suspend - this function will run on a background thread instead of the main thread
    @QueryMap - lets us create something like a HashMap to add all of our queries at once
    Response - a simple class for an http response
     */
    @GET("/recipes/complexSearch")
    suspend fun getRecipe(
        @QueryMap queryMap: Map<String, String>
    ): Response<Recipe>

    @GET("/recipes/complexSearch")
    suspend fun searchRecipe(
        @QueryMap searchQueryMap: Map<String, String>
    ): Response<Recipe>

    @GET("/food/jokes/random")
    suspend fun getJoke(
        @Query("apiKey") apiKey: String
    ): Response<Joke>

}