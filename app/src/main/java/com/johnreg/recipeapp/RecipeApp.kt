package com.johnreg.recipeapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/** This is the place where the dagger components should be generated automatically for us. */
@HiltAndroidApp
class RecipeApp : Application()