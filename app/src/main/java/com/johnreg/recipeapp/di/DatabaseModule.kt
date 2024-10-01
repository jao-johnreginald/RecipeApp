package com.johnreg.recipeapp.di

import android.content.Context
import androidx.room.Room
import com.johnreg.recipeapp.data.local.RecipeDao
import com.johnreg.recipeapp.data.local.RecipeDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideRecipeDao(
        recipeDatabase: RecipeDatabase
    ): RecipeDao {
        return recipeDatabase.getRecipeDao()
    }

    @Singleton
    @Provides
    fun provideRecipeDatabase(
        @ApplicationContext context: Context
    ): RecipeDatabase {
        return Room.databaseBuilder(
            context,
            RecipeDatabase::class.java,
            "recipe_database"
        ).build()
    }

}