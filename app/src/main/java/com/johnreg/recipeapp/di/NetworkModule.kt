package com.johnreg.recipeapp.di

import com.johnreg.recipeapp.RecipeApi
import com.johnreg.recipeapp.utils.Constants.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    /*
    @Module - tells dagger that this object is a module
    @InstallIn - tells dagger how long these dependencies will live
    SingletonComponent - we want these dependencies to live as long as our application does

    @Singleton - we're going to have only one instance for each of those dependencies

    @Provides - the return type of those functions are actually telling our hilt library
    that we have specified already how to create an instance of those dependencies

    In order to provide this RecipeApi, we need to provide all the dependencies before that
    RecipeApi <- Retrofit <- OkHttpClient & GsonConverterFactory
     */
    @Singleton
    @Provides
    fun provideRecipeApi(retrofit: Retrofit): RecipeApi {
        return retrofit.create(RecipeApi::class.java)
    }

    @Singleton
    @Provides
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        gsonConverterFactory: GsonConverterFactory
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(gsonConverterFactory)
            .build()
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .readTimeout(15, TimeUnit.SECONDS)
            .connectTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    @Singleton
    @Provides
    fun provideGsonConverterFactory(): GsonConverterFactory {
        return GsonConverterFactory.create()
    }

}