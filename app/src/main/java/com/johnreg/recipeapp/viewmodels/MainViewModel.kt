package com.johnreg.recipeapp.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.johnreg.recipeapp.data.entities.RecipeEntity
import com.johnreg.recipeapp.data.repositories.MainRepository
import com.johnreg.recipeapp.models.Recipe
import com.johnreg.recipeapp.utils.NetworkCallback
import com.johnreg.recipeapp.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: MainRepository,
    private val networkCallback: NetworkCallback,
    application: Application
) : AndroidViewModel(application) {

    /** LOCAL DATABASE */
    val recipes: LiveData<List<RecipeEntity>> = repository.local.getRecipes().asLiveData()

    private fun insertRecipe(recipeEntity: RecipeEntity) = viewModelScope.launch(Dispatchers.IO) {
        repository.local.insertRecipe(recipeEntity)
    }

    /** REMOTE API */
    private val _recipeResponse: MutableLiveData<NetworkResult<Recipe>> = MutableLiveData()
    private val _searchResponse: MutableLiveData<NetworkResult<Recipe>> = MutableLiveData()

    val recipeResponse: LiveData<NetworkResult<Recipe>> get() = _recipeResponse
    val searchResponse: LiveData<NetworkResult<Recipe>> get() = _searchResponse

    fun getRecipe(queryMap: Map<String, String>) = viewModelScope.launch {
        // Set the value of the MutableLiveData
        _recipeResponse.value = NetworkResult.Loading()
        _recipeResponse.value = if (networkCallback.isNetworkAvailable().first()) {
            try {
                val response = repository.remote.getRecipe(queryMap)
                handleRecipeResponse(response)
            } catch (e: Exception) {
                Log.e("RecipesFragment", e.localizedMessage, e)
                NetworkResult.Error("Recipes Not Found.\n${e.localizedMessage?.uppercase()}")
            }
        } else {
            NetworkResult.Error("No Internet Connection.")
        }

        // At this stage the value has already been set, if the data is not null, cache the data
        _recipeResponse.value!!.data?.let { recipe ->
            insertRecipe(RecipeEntity(recipe))
        }
    }

    fun searchRecipe(searchQueryMap: Map<String, String>) = viewModelScope.launch {
        _searchResponse.value = NetworkResult.Loading()
        _searchResponse.value = if (networkCallback.isNetworkAvailable().first()) {
            try {
                val response = repository.remote.searchRecipe(searchQueryMap)
                handleRecipeResponse(response)
            } catch (e: Exception) {
                Log.e("RecipesFragment", e.localizedMessage, e)
                NetworkResult.Error("Recipes Not Found.\n${e.localizedMessage?.uppercase()}")
            }
        } else {
            NetworkResult.Error("No Internet Connection.")
        }
    }

    private fun handleRecipeResponse(response: Response<Recipe>): NetworkResult<Recipe> {
        Log.d("RecipesFragment", "Request URL: ${response.raw().request.url}")
        val recipe = response.body()
        return when {
            response.message().contains("timeout") -> NetworkResult.Error("Timeout.")
            response.code() == 402 -> NetworkResult.Error("API Key Limited.")
            recipe?.results!!.isEmpty() -> NetworkResult.Error("Recipes Not Found.")

            response.isSuccessful -> NetworkResult.Success(recipe)

            else -> NetworkResult.Error(response.message())
        }
    }

}