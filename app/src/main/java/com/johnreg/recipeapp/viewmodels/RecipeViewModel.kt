package com.johnreg.recipeapp.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.johnreg.recipeapp.data.repositories.DataStoreRepository
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipeViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    application: Application
) : AndroidViewModel(application) {

    private var mealType = DEFAULT_MEAL_TYPE
    private var dietType = DEFAULT_DIET_TYPE

    val types = dataStoreRepository.getTypesPreferences()

    fun saveMealAndDietType(
        mealTypeName: String, mealTypeId: Int, dietTypeName: String, dietTypeId: Int
    ) = viewModelScope.launch(Dispatchers.IO) {
        dataStoreRepository.setTypesPreferences(mealTypeName, mealTypeId, dietTypeName, dietTypeId)
    }

    fun getQueryMap(): HashMap<String, String> {
        viewModelScope.launch {
            // Collect the values from that Flow and store them inside those variables
            types.collect { types ->
                mealType = types.mealTypeName
                dietType = types.dietTypeName
            }
        }

        // Get the newest values inside our getQueryMap() every time
        // If there is no data inside those types, get their default values
        return hashMapOf(
            QUERY_NUMBER to DEFAULT_RESULT_COUNT,
            QUERY_API_KEY to API_KEY,
            QUERY_TYPE to mealType,
            QUERY_DIET to dietType,
            QUERY_ADD_RECIPE_INFORMATION to "true",
            QUERY_FILL_INGREDIENTS to "true"
        )
    }

}