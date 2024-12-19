package com.johnreg.recipeapp.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.johnreg.recipeapp.data.entities.FavoriteEntity
import com.johnreg.recipeapp.data.entities.JokeEntity
import com.johnreg.recipeapp.data.entities.RecipeEntity
import com.johnreg.recipeapp.data.repositories.MainRepository
import com.johnreg.recipeapp.models.Joke
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
    val favorites: LiveData<List<FavoriteEntity>> = repository.local.getFavorites().asLiveData()
    val jokes: LiveData<List<JokeEntity>> = repository.local.getJokes().asLiveData()

    private fun insertRecipe(recipeEntity: RecipeEntity) = viewModelScope.launch(Dispatchers.IO) {
        repository.local.insertRecipe(recipeEntity)
    }

    fun insertFavorite(favoriteEntity: FavoriteEntity) = viewModelScope.launch(Dispatchers.IO) {
        repository.local.insertFavorite(favoriteEntity)
    }

    private fun insertJoke(jokeEntity: JokeEntity) = viewModelScope.launch(Dispatchers.IO) {
        repository.local.insertJoke(jokeEntity)
    }

    fun deleteFavorite(favoriteEntity: FavoriteEntity) = viewModelScope.launch(Dispatchers.IO) {
        repository.local.deleteFavorite(favoriteEntity)
    }

    fun deleteAllFavorites() = viewModelScope.launch(Dispatchers.IO) {
        repository.local.deleteAllFavorites()
    }

    /** REMOTE API */
    private val _recipeResponse: MutableLiveData<NetworkResult<Recipe>> = MutableLiveData()
    private val _searchResponse: MutableLiveData<NetworkResult<Recipe>> = MutableLiveData()
    private val _jokeResponse: MutableLiveData<NetworkResult<Joke>> = MutableLiveData()

    val recipeResponse: LiveData<NetworkResult<Recipe>> get() = _recipeResponse
    val searchResponse: LiveData<NetworkResult<Recipe>> get() = _searchResponse
    val jokeResponse: LiveData<NetworkResult<Joke>> get() = _jokeResponse

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

    fun getJoke(apiKey: String) = viewModelScope.launch {
        _jokeResponse.value = NetworkResult.Loading()
        _jokeResponse.value = if (networkCallback.isNetworkAvailable().first()) {
            try {
                val response = repository.remote.getJoke(apiKey)
                Log.d("JokesFragment", "Request URL: ${response.raw().request.url}")
                val joke = response.body()
                when {
                    response.message().contains("timeout") -> NetworkResult.Error("Timeout.")
                    response.code() == 402 -> NetworkResult.Error("API Key Limited.")
                    joke?.text!!.isEmpty() -> NetworkResult.Error("Joke Not Found.")

                    response.isSuccessful -> NetworkResult.Success(joke)

                    else -> NetworkResult.Error(response.message())
                }
            } catch (e: Exception) {
                Log.e("JokesFragment", e.localizedMessage, e)
                NetworkResult.Error("Joke Not Found.\n${e.localizedMessage?.uppercase()}")
            }
        } else {
            NetworkResult.Error("No Internet Connection.")
        }

        _jokeResponse.value!!.data?.let { joke ->
            insertJoke(JokeEntity(joke))
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