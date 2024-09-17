package com.johnreg.recipeapp

import com.johnreg.recipeapp.models.Recipe
import retrofit2.Response
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    /*
    By annotating this @Inject constructor and specifying this type which we want to inject, hilt will
    basically search for this specific type in this NetworkModule and it will search for a function
    which returns this same type, and that's how it will know how to create an instance of RecipeApi
     */
    private val recipeApi: RecipeApi
) {

    suspend fun getRecipe(queries: Map<String, String>): Response<Recipe> {
        return recipeApi.getRecipe(queries)
    }

}