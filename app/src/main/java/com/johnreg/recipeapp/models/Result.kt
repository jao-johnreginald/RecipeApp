package com.johnreg.recipeapp.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class Result(
    @SerializedName("extendedIngredients") val ingredients: @RawValue List<Ingredient>,
    @SerializedName("id") val id: Int,

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
) : Parcelable