package com.johnreg.recipeapp.data.local

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.johnreg.recipeapp.models.Recipe
import javax.inject.Inject

class RecipeConverter @Inject constructor(
    private val gson: Gson
) {

    @TypeConverter
    fun recipeToString(recipe: Recipe): String {
        return gson.toJson(recipe)
    }

    @TypeConverter
    fun stringToRecipe(string: String): Recipe {
        val listType = object : TypeToken<Recipe>() {}.type
        return gson.fromJson(string, listType)
    }

}