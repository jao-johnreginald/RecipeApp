package com.johnreg.recipeapp.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.johnreg.recipeapp.databinding.FragmentRecipesBinding
import com.johnreg.recipeapp.ui.adapters.RecipesAdapter
import com.johnreg.recipeapp.utils.NetworkResult
import com.johnreg.recipeapp.viewmodels.MainViewModel
import com.johnreg.recipeapp.viewmodels.RecipeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RecipesFragment : Fragment() {

    private lateinit var binding: FragmentRecipesBinding

    private val recipesAdapter by lazy { RecipesAdapter() }

    private val mainViewModel: MainViewModel by viewModels()
    private val recipeViewModel: RecipeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecipesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setRecyclerView()
        checkDatabase()
    }

    private fun setRecyclerView() {
        binding.rvRecipes.apply {
            adapter = recipesAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun checkDatabase() {
        lifecycleScope.launch {
            mainViewModel.recipes.observe(viewLifecycleOwner) { database ->
                // If database is not empty, load data from cache, if it is, request a new data
                if (database.isNotEmpty()) {
                    Log.d("RecipesFragment", "inside database.isNotEmpty()")
                    binding.shimmerFrameLayout.visibility = View.INVISIBLE
                    val recipe = database[0].recipe
                    recipesAdapter.setResults(recipe)
                } else requestApiData()
            }
        }
    }

    private fun requestApiData() {
        Log.d("RecipesFragment", "inside requestApiData()")
        mainViewModel.getRecipe(recipeViewModel.getQueryMap())

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

}