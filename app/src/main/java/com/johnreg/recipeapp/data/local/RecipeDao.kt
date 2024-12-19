package com.johnreg.recipeapp.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.johnreg.recipeapp.data.entities.FavoriteEntity
import com.johnreg.recipeapp.data.entities.JokeEntity
import com.johnreg.recipeapp.data.entities.RecipeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {

    @Query("SELECT * FROM recipe_table ORDER BY id ASC")
    fun getRecipes(): Flow<List<RecipeEntity>>

    @Query("SELECT * FROM favorites_table ORDER BY id ASC")
    fun getFavorites(): Flow<List<FavoriteEntity>>

    @Query("SELECT * FROM joke_table ORDER BY id ASC")
    fun getJokes(): Flow<List<JokeEntity>>

    // REPLACE - whenever we fetch a new data from our api, we want to replace the old one
    // Our table will have the newest recipes everytime we request a new data from our api
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipe(recipeEntity: RecipeEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favoriteEntity: FavoriteEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJoke(jokeEntity: JokeEntity)

    @Delete
    suspend fun deleteFavorite(favoriteEntity: FavoriteEntity)

    @Query("DELETE FROM favorites_table")
    suspend fun deleteAllFavorites()

}