package com.johnreg.recipeapp.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.johnreg.recipeapp.models.Result

@Entity(tableName = "favorites_table")
data class FavoriteEntity(
    val result: Result,
    // (autoGenerate = true) because we're going to have multiple favorites inside our favorites_table
    @PrimaryKey(autoGenerate = true) val id: Int = 0
)