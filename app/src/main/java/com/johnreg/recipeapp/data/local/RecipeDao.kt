package com.johnreg.recipeapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.johnreg.recipeapp.data.entities.RecipeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {

    @Query("SELECT * FROM recipe_table ORDER BY id ASC")
    fun getRecipes(): Flow<List<RecipeEntity>>

    // REPLACE - whenever we fetch a new data from our api, we want to replace the old one
    // Our table will have the newest recipes everytime we request a new data from our api
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipe(recipeEntity: RecipeEntity)

}