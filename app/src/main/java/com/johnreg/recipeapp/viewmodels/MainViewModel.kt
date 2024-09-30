package com.johnreg.recipeapp.viewmodels

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.johnreg.recipeapp.data.repositories.MainRepository
import com.johnreg.recipeapp.models.Recipe
import com.johnreg.recipeapp.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: MainRepository,
    application: Application
) : AndroidViewModel(application) {

    private val _recipeResponse: MutableLiveData<NetworkResult<Recipe>> = MutableLiveData()
    val recipeResponse: LiveData<NetworkResult<Recipe>> get() = _recipeResponse

    fun getRecipe(queryMap: Map<String, String>) = viewModelScope.launch {
        _recipeResponse.value = NetworkResult.Loading()
        _recipeResponse.value = if (hasInternetConnection()) {
            try {
                val response = repository.remote.getRecipe(queryMap)
                val recipe = response.body()!!
                when {
                    response.message().contains("timeout") -> NetworkResult.Error("Timeout.")
                    response.code() == 402 -> NetworkResult.Error("API Key Limited.")
                    recipe.results.isEmpty() -> NetworkResult.Error("Recipes not found.")

                    response.isSuccessful -> NetworkResult.Success(recipe)

                    else -> NetworkResult.Error(response.message())
                }
            } catch (e: Exception) {
                NetworkResult.Error("Recipes not found. ${e.localizedMessage}")
            }
        } else {
            NetworkResult.Error("No Internet Connection.")
        }
    }

    private fun hasInternetConnection(): Boolean {
        val connectivityManager = getApplication<Application>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager

        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false

        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }

}