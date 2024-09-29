package com.johnreg.recipeapp.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.johnreg.recipeapp.utils.Constants.API_KEY
import com.johnreg.recipeapp.utils.Constants.QUERY_ADD_RECIPE_INFORMATION
import com.johnreg.recipeapp.utils.Constants.QUERY_API_KEY
import com.johnreg.recipeapp.utils.Constants.QUERY_DIET
import com.johnreg.recipeapp.utils.Constants.QUERY_FILL_INGREDIENTS
import com.johnreg.recipeapp.utils.Constants.QUERY_NUMBER
import com.johnreg.recipeapp.utils.Constants.QUERY_TYPE
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RecipeViewModel @Inject constructor(
    application: Application
) : AndroidViewModel(application) {

    fun getQueries(): HashMap<String, String> = hashMapOf(
        QUERY_NUMBER to "50",
        QUERY_API_KEY to API_KEY,
        QUERY_TYPE to "snack",
        QUERY_DIET to "vegan",
        QUERY_ADD_RECIPE_INFORMATION to "true",
        QUERY_FILL_INGREDIENTS to "true"
    )

}