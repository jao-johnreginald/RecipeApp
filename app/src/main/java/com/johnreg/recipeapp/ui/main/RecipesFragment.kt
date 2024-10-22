package com.johnreg.recipeapp.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.johnreg.recipeapp.R
import com.johnreg.recipeapp.databinding.FragmentRecipesBinding
import com.johnreg.recipeapp.ui.adapters.RecipesAdapter
import com.johnreg.recipeapp.utils.NetworkResult
import com.johnreg.recipeapp.utils.observeOnce
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

    private val args: RecipesFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecipesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setRvAndFab()
        checkDatabaseAndArgs()
    }

    private fun setRvAndFab() {
        binding.rvRecipes.apply {
            adapter = recipesAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        binding.fabRecipes.setOnClickListener {
            findNavController().navigate(R.id.action_recipesFragment_to_recipesBottomSheet)
        }
    }

    private fun checkDatabaseAndArgs() {
        lifecycleScope.launch {
            mainViewModel.recipes.observeOnce(viewLifecycleOwner) { database ->
                if (database.isEmpty() || args.isApplyButtonClicked) {
                    requestApiData()
                } else {
                    Log.d("RecipesFragment", "inside else block")
                    binding.shimmerFrameLayout.visibility = View.INVISIBLE
                    val recipe = database.first().recipe
                    recipesAdapter.setResults(recipe)
                }
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