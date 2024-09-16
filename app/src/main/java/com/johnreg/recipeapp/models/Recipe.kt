package com.johnreg.recipeapp.models

import com.google.gson.annotations.SerializedName

data class Recipe(
    @SerializedName("results") val results: List<Result>
)