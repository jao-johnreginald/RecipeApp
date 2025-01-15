package com.johnreg.recipeapp.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.johnreg.recipeapp.data.models.Recipe

@Entity(tableName = "recipe_table")
data class RecipeEntity(
    val recipe: Recipe,
    /*
    @PrimaryKey(autoGenerate = false) - whenever we fetch a new data from our api, we're going to replace
    all data from our database table with a new data, so we're not going to have multiple recipes, we're
    going to have only one and we're going to replace that one whenever we fetch a new data, whenever
    our application reads our database, it will fetch the newest data which we have in our database
     */
    @PrimaryKey(autoGenerate = false) val id: Int = 0
)