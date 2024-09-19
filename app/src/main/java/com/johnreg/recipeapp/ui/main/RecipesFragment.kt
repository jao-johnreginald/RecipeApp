package com.johnreg.recipeapp.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.johnreg.recipeapp.databinding.FragmentRecipesBinding
import com.johnreg.recipeapp.ui.adapters.RecipesAdapter
import com.johnreg.recipeapp.utils.Constants.API_KEY
import com.johnreg.recipeapp.utils.NetworkResult
import com.johnreg.recipeapp.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecipesFragment : Fragment() {

    private lateinit var binding: FragmentRecipesBinding

    private val recipesAdapter by lazy { RecipesAdapter() }

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecipesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setRecyclerView()
        requestApiData()
    }

    private fun setRecyclerView() {
        binding.rvRecipes.apply {
            adapter = recipesAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun requestApiData() {
        mainViewModel.getRecipe(getQueries())
        mainViewModel.recipeResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    binding.shimmerFrameLayout.visibility = View.INVISIBLE
                    response.data?.let { recipe ->
                        recipesAdapter.setResults(recipe)
                    }
                }

                is NetworkResult.Error -> {
                    binding.shimmerFrameLayout.visibility = View.INVISIBLE
                    binding.ivError.visibility = View.VISIBLE
                    binding.tvError.visibility = View.VISIBLE
                    binding.tvError.text = response.message
                }

                is NetworkResult.Loading -> {
                    binding.shimmerFrameLayout.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun getQueries(): HashMap<String, String> = hashMapOf(
        "number" to "50",
        "apiKey" to API_KEY,
        "type" to "snack",
        "diet" to "vegan",
        "addRecipeInformation" to "true",
        "fillIngredients" to "true"
    )

}