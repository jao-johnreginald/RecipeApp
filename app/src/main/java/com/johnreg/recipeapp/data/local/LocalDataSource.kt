package com.johnreg.recipeapp.data.local

import com.johnreg.recipeapp.data.entities.RecipeEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalDataSource @Inject constructor(
    private val recipeDao: RecipeDao
) {

    fun getRecipes(): Flow<List<RecipeEntity>> = recipeDao.getRecipes()

    suspend fun insertRecipe(recipeEntity: RecipeEntity) = recipeDao.insertRecipe(recipeEntity)

}