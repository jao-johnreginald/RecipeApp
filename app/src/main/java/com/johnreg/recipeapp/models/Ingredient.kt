package com.johnreg.recipeapp.models

import com.google.gson.annotations.SerializedName

data class Ingredient(
    @SerializedName("image") val imageUrl: String,
    @SerializedName("name") val name: String,
    @SerializedName("amount") val amount: Double,
    @SerializedName("unit") val unit: String,
    @SerializedName("consistency") val consistency: String,
    @SerializedName("original") val original: String
)