package com.johnreg.recipeapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.johnreg.recipeapp.data.entities.RecipeEntity

@Database(
    entities = [RecipeEntity::class],
    version = 1, exportSchema = false
)
@TypeConverters(RecipeConverter::class)
abstract class RecipeDatabase : RoomDatabase() {

    abstract fun getRecipeDao(): RecipeDao

}