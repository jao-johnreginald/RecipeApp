package com.johnreg.recipeapp.data.repositories

import com.johnreg.recipeapp.data.local.LocalDataSource
import com.johnreg.recipeapp.data.remote.RemoteDataSource
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject

/*
@ActivityRetainedScoped - a scope annotation for bindings that should exist
for the life of an activity, and survive a configuration change as well

Since we're going to inject this Repository in a ViewModel later, I have just added this
@ActivityRetainedScoped so this Repository can survive a configuration change as well
and we're going to have the same instance even when a user is rotating the screen
 */
@ActivityRetainedScoped
data class MainRepository @Inject constructor(
    val local: LocalDataSource,
    val remote: RemoteDataSource
)