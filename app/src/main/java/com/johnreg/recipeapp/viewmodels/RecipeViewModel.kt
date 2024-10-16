package com.johnreg.recipeapp.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.johnreg.recipeapp.utils.Constants.API_KEY
import com.johnreg.recipeapp.utils.Constants.DEFAULT_DIET_TYPE
import com.johnreg.recipeapp.utils.Constants.DEFAULT_MEAL_TYPE
import com.johnreg.recipeapp.utils.Constants.DEFAULT_RESULT_COUNT
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

    fun getQueryMap(): HashMap<String, String> = hashMapOf(
        QUERY_NUMBER to DEFAULT_RESULT_COUNT,
        QUERY_API_KEY to API_KEY,
        QUERY_TYPE to DEFAULT_MEAL_TYPE,
        QUERY_DIET to DEFAULT_DIET_TYPE,
        QUERY_ADD_RECIPE_INFORMATION to "true",
        QUERY_FILL_INGREDIENTS to "true"
    )

}