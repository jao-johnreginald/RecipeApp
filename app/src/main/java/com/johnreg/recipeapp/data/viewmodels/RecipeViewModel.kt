package com.johnreg.recipeapp.data.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.johnreg.recipeapp.data.repositories.DataStoreRepository
import com.johnreg.recipeapp.data.repositories.NetworkCallback
import com.johnreg.recipeapp.data.repositories.Types
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipeViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    networkCallback: NetworkCallback
) : ViewModel() {

    val types: LiveData<Types> = dataStoreRepository.getTypes().asLiveData()

    val isNetworkAvailable: LiveData<Boolean> = networkCallback.isNetworkAvailable().asLiveData()

    fun setTypes(
        mealTypeName: String, mealTypeId: Int, dietTypeName: String, dietTypeId: Int
    ) = viewModelScope.launch(Dispatchers.IO) {
        dataStoreRepository.setTypes(mealTypeName, mealTypeId, dietTypeName, dietTypeId)
    }

}