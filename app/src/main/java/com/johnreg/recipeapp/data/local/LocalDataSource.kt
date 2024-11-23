package com.johnreg.recipeapp.data.local

import com.johnreg.recipeapp.data.entities.FavoriteEntity
import com.johnreg.recipeapp.data.entities.RecipeEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalDataSource @Inject constructor(
    private val recipeDao: RecipeDao
) {

    fun getRecipes(): Flow<List<RecipeEntity>> = recipeDao.getRecipes()

    fun getFavorites(): Flow<List<FavoriteEntity>> = recipeDao.getFavorites()

    suspend fun insertRecipe(
        recipeEntity: RecipeEntity
    ) = recipeDao.insertRecipe(recipeEntity)

    suspend fun insertFavorite(
        favoriteEntity: FavoriteEntity
    ) = recipeDao.insertFavorite(favoriteEntity)

    suspend fun deleteFavorite(
        favoriteEntity: FavoriteEntity
    ) = recipeDao.deleteFavorite(favoriteEntity)

    suspend fun deleteAllFavorites() = recipeDao.deleteAllFavorites()

}