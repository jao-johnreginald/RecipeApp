package com.johnreg.recipeapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.johnreg.recipeapp.data.repositories.DataStoreRepository
import com.johnreg.recipeapp.utils.NetworkCallback
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipeViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    networkCallback: NetworkCallback
) : ViewModel() {

    val isNetworkAvailable = networkCallback.isNetworkAvailable().asLiveData()

    val types = dataStoreRepository.getTypesPreferences().asLiveData()

    fun saveMealAndDietType(
        mealTypeName: String, mealTypeId: Int, dietTypeName: String, dietTypeId: Int
    ) = viewModelScope.launch(Dispatchers.IO) {
        dataStoreRepository.setTypesPreferences(mealTypeName, mealTypeId, dietTypeName, dietTypeId)
    }

}