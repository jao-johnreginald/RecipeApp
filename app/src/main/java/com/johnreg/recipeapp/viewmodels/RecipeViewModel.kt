package com.johnreg.recipeapp.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.johnreg.recipeapp.data.repositories.DataStoreRepository
import com.johnreg.recipeapp.utils.Constants.API_KEY
import com.johnreg.recipeapp.utils.Constants.DEFAULT_RESULT_COUNT
import com.johnreg.recipeapp.utils.Constants.QUERY_ADD_RECIPE_INFORMATION
import com.johnreg.recipeapp.utils.Constants.QUERY_API_KEY
import com.johnreg.recipeapp.utils.Constants.QUERY_DIET
import com.johnreg.recipeapp.utils.Constants.QUERY_FILL_INGREDIENTS
import com.johnreg.recipeapp.utils.Constants.QUERY_NUMBER
import com.johnreg.recipeapp.utils.Constants.QUERY_TYPE
import com.johnreg.recipeapp.utils.NetworkCallback
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipeViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    networkCallback: NetworkCallback,
    application: Application
) : AndroidViewModel(application) {

    val isNetworkAvailable = networkCallback.isNetworkAvailable().asLiveData()

    val types = dataStoreRepository.getTypesPreferences().asLiveData()

    fun saveMealAndDietType(
        mealTypeName: String, mealTypeId: Int, dietTypeName: String, dietTypeId: Int
    ) = viewModelScope.launch(Dispatchers.IO) {
        dataStoreRepository.setTypesPreferences(mealTypeName, mealTypeId, dietTypeName, dietTypeId)
    }

    fun getQueryMap(
        mealType: String, dietType: String
    ): HashMap<String, String> = hashMapOf(
        QUERY_NUMBER to DEFAULT_RESULT_COUNT,
        QUERY_API_KEY to API_KEY,
        QUERY_TYPE to mealType,
        QUERY_DIET to dietType,
        QUERY_ADD_RECIPE_INFORMATION to "true",
        QUERY_FILL_INGREDIENTS to "true"
    )

}