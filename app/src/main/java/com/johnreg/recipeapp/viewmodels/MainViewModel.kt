package com.johnreg.recipeapp.viewmodels

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.johnreg.recipeapp.data.entities.RecipeEntity
import com.johnreg.recipeapp.data.repositories.MainRepository
import com.johnreg.recipeapp.models.Recipe
import com.johnreg.recipeapp.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: MainRepository,
    application: Application
) : AndroidViewModel(application) {

    /** LOCAL DATABASE */
    val recipes: LiveData<List<RecipeEntity>> = repository.local.getRecipes().asLiveData()

    private fun insertRecipe(recipeEntity: RecipeEntity) = viewModelScope.launch(Dispatchers.IO) {
        repository.local.insertRecipe(recipeEntity)
    }

    /** REMOTE API */
    private val _recipeResponse: MutableLiveData<NetworkResult<Recipe>> = MutableLiveData()
    val recipeResponse: LiveData<NetworkResult<Recipe>> get() = _recipeResponse

    fun getRecipe(queryMap: Map<String, String>) = viewModelScope.launch {
        // Set the value of the MutableLiveData
        _recipeResponse.value = NetworkResult.Loading()
        _recipeResponse.value = if (hasInternetConnection()) {
            try {
                val response = repository.remote.getRecipe(queryMap)
                val recipe = response.body()!!
                Log.d("RecipesFragment", "Request URL: ${response.raw().request.url}")
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

        // At this stage the value has already been set, if the data is not null, cache the data
        _recipeResponse.value!!.data?.let { recipe ->
            insertRecipe(RecipeEntity(recipe))
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