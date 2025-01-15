package com.johnreg.recipeapp.data.local

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.johnreg.recipeapp.data.models.Recipe
import com.johnreg.recipeapp.data.models.Result

class RecipeConverter {

    private val gson: Gson = Gson()

    @TypeConverter
    fun recipeToString(recipe: Recipe): String {
        return gson.toJson(recipe)
    }

    @TypeConverter
    fun stringToRecipe(string: String): Recipe {
        val listType = object : TypeToken<Recipe>() {}.type
        return gson.fromJson(string, listType)
    }

    @TypeConverter
    fun resultToString(result: Result): String {
        return gson.toJson(result)
    }

    @TypeConverter
    fun stringToResult(string: String): Result {
        val listType = object : TypeToken<Result>() {}.type
        return gson.fromJson(string, listType)
    }

}