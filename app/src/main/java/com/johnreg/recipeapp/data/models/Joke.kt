package com.johnreg.recipeapp.data.models

import com.google.gson.annotations.SerializedName

data class Joke(
    @SerializedName("text") val text: String
)