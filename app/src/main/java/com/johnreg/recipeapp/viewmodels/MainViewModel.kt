package com.johnreg.recipeapp.viewmodels

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.johnreg.recipeapp.MainRepository
import com.johnreg.recipeapp.models.Recipe
import com.johnreg.recipeapp.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: MainRepository,
    application: Application
) : AndroidViewModel(application) {

    private val _recipeResponse: MutableLiveData<NetworkResult<Recipe>> = MutableLiveData()
    val recipeResponse: LiveData<NetworkResult<Recipe>> get() = _recipeResponse

    fun getRecipe(queries: Map<String, String>) = viewModelScope.launch {
        getRecipeSafeCall(queries)
    }

    private suspend fun getRecipeSafeCall(queries: Map<String, String>) {
        _recipeResponse.value = NetworkResult.Loading()
        if (hasInternetConnection()) {
            try {
                val response = repository.remote.getRecipe(queries)
                _recipeResponse.value = handleRecipeResponse(response)
            } catch (e: Exception) {
                _recipeResponse.value = NetworkResult.Error(
                    "Recipes not found. ${e.localizedMessage}"
                )
            }
        } else {
            _recipeResponse.value = NetworkResult.Error("No Internet Connection.")
        }
    }

    private fun handleRecipeResponse(response: Response<Recipe>): NetworkResult<Recipe> {
        return when {
            response.message().toString().contains("timeout") -> NetworkResult.Error("Timeout.")

            response.code() == 402 -> NetworkResult.Error("API Key Limited.")

            response.body()!!.results.isEmpty() -> NetworkResult.Error("Recipes not found.")

            response.isSuccessful -> {
                val recipe = response.body()
                NetworkResult.Success(recipe!!)
            }

            else -> NetworkResult.Error(response.message())
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