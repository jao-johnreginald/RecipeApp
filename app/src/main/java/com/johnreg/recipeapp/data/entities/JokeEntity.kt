package com.johnreg.recipeapp.data.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.johnreg.recipeapp.data.models.Joke

@Entity(tableName = "joke_table")
data class JokeEntity(
    // @Embedded - inspect our Joke model class, take its field, and store that inside our new table
    @Embedded val joke: Joke,
    // We don't need to autoGenerate this id because we're going to have only one row
    // and that row will be updated anytime we request a new data from our api
    @PrimaryKey(autoGenerate = false) val id: Int = 0
)