package com.johnreg.recipeapp.models

import com.google.gson.annotations.SerializedName

data class Result(
    @SerializedName("extendedIngredients") val ingredients: List<Ingredient>,
    @SerializedName("id") val recipeId: Int,

    @SerializedName("image") val imageUrl: String,
    @SerializedName("title") val title: String,
    @SerializedName("summary") val description: String,
    @SerializedName("aggregateLikes") val totalLikes: Int,
    @SerializedName("readyInMinutes") val totalMinutes: Int,
    @SerializedName("vegan") val isVegan: Boolean,

    @SerializedName("sourceName") val sourceName: String?,
    @SerializedName("sourceUrl") val sourceUrl: String,

    @SerializedName("vegetarian") val isVegetarian: Boolean,
    @SerializedName("glutenFree") val isGlutenFree: Boolean,
    @SerializedName("veryHealthy") val isHealthy: Boolean,
    @SerializedName("dairyFree") val isDairyFree: Boolean,
    @SerializedName("cheap") val isCheap: Boolean
)