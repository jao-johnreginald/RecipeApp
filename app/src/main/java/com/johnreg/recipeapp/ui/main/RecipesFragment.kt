package com.johnreg.recipeapp.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.johnreg.recipeapp.R
import com.johnreg.recipeapp.data.models.Recipe
import com.johnreg.recipeapp.data.viewmodels.MainViewModel
import com.johnreg.recipeapp.data.viewmodels.RecipeViewModel
import com.johnreg.recipeapp.databinding.FragmentRecipesBinding
import com.johnreg.recipeapp.ui.adapters.RecipesAdapter
import com.johnreg.recipeapp.utils.Constants.API_KEY
import com.johnreg.recipeapp.utils.Constants.DEFAULT_RESULT_COUNT
import com.johnreg.recipeapp.utils.Constants.QUERY_ADD_RECIPE_INFORMATION
import com.johnreg.recipeapp.utils.Constants.QUERY_API_KEY
import com.johnreg.recipeapp.utils.Constants.QUERY_DIET
import com.johnreg.recipeapp.utils.Constants.QUERY_FILL_INGREDIENTS
import com.johnreg.recipeapp.utils.Constants.QUERY_NUMBER
import com.johnreg.recipeapp.utils.Constants.QUERY_SEARCH
import com.johnreg.recipeapp.utils.Constants.QUERY_TYPE
import com.johnreg.recipeapp.utils.NetworkResult
import com.johnreg.recipeapp.utils.observeOnce
import com.johnreg.recipeapp.utils.setErrorTextAndListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecipesFragment : Fragment() {

    private var _binding: FragmentRecipesBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel: MainViewModel by viewModels()
    private val recipeViewModel: RecipeViewModel by viewModels()

    private val args: RecipesFragmentArgs by navArgs()

    private val recipesAdapter = RecipesAdapter()

    private var isDataRequested = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecipesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setMenu()
        setRvAndFab()
        checkDatabase()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setMenu() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.recipes_menu, menu)
                setSearchView(menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean = false

            private fun setSearchView(menu: Menu) {
                val searchView = menu.findItem(R.id.menu_search).actionView as? SearchView
                searchView?.isSubmitButtonEnabled = true

                searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        if (query != null) searchApiData(query)
                        return false
                    }

                    override fun onQueryTextChange(newText: String?): Boolean = false

                    private fun searchApiData(searchQuery: String) {
                        mainViewModel.searchResponse.observe(viewLifecycleOwner) { response ->
                            handleRecipeResponse(response)
                        }

                        val searchQueryMap = hashMapOf(
                            QUERY_SEARCH to searchQuery,
                            QUERY_NUMBER to DEFAULT_RESULT_COUNT,
                            QUERY_API_KEY to API_KEY,
                            QUERY_ADD_RECIPE_INFORMATION to "true",
                            QUERY_FILL_INGREDIENTS to "true"
                        )

                        mainViewModel.searchRecipe(searchQueryMap)
                    }
                })
            }
        }, viewLifecycleOwner, Lifecycle.State.STARTED)
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

    private fun checkDatabase() {
        mainViewModel.recipes.observeOnce(viewLifecycleOwner) { database ->
            if (args.isApplyButtonClicked) {
                if (!isDataRequested) {
                    isDataRequested = true
                    requestApiData()
                    Log.d("RecipesFragment", "Apply button clicked! New API data requested!")
                } else {
                    if (database.isNotEmpty()) setRecipesAdapter(database.first().recipe)
                    else requestApiData()
                    Log.d("RecipesFragment", "Apply button clicked! Data already requested!")
                }
            } else {
                if (database.isNotEmpty()) setRecipesAdapter(database.first().recipe)
                else requestApiData()
                Log.d("RecipesFragment", "Apply button not clicked!")
            }
        }
    }

    private fun requestApiData() {
        mainViewModel.recipeResponse.observe(viewLifecycleOwner) { response ->
            handleRecipeResponse(response)
        }

        recipeViewModel.types.observe(viewLifecycleOwner) { types ->
            val queryMap = hashMapOf(
                QUERY_NUMBER to DEFAULT_RESULT_COUNT,
                QUERY_API_KEY to API_KEY,
                QUERY_TYPE to types.mealTypeName,
                QUERY_DIET to types.dietTypeName,
                QUERY_ADD_RECIPE_INFORMATION to "true",
                QUERY_FILL_INGREDIENTS to "true"
            )

            mainViewModel.getRecipe(queryMap)
            Log.d("RecipesFragment", types.toString())
        }
    }

    private fun handleRecipeResponse(response: NetworkResult<Recipe>) {
        when (response) {
            is NetworkResult.Success -> setRecipesAdapter(response.data)

            is NetworkResult.Error -> {
                binding.shimmerFrameLayout.visibility = View.INVISIBLE
                binding.rvRecipes.visibility = View.INVISIBLE
                binding.ivError.visibility = View.VISIBLE
                binding.tvError.visibility = View.VISIBLE

                binding.tvError.setErrorTextAndListener(response.message) { textView ->
                    mainViewModel.recipes.observeOnce(viewLifecycleOwner) { database ->
                        if (database.isNotEmpty()) setRecipesAdapter(database.first().recipe)
                        else textView.text = getString(R.string.cache_is_empty)
                    }
                }
            }

            is NetworkResult.Loading -> {
                binding.shimmerFrameLayout.visibility = View.VISIBLE
                binding.rvRecipes.visibility = View.INVISIBLE
                binding.ivError.visibility = View.INVISIBLE
                binding.tvError.visibility = View.INVISIBLE
            }
        }
    }

    private fun setRecipesAdapter(recipe: Recipe?) {
        binding.shimmerFrameLayout.visibility = View.INVISIBLE
        binding.rvRecipes.visibility = View.VISIBLE
        binding.ivError.visibility = View.INVISIBLE
        binding.tvError.visibility = View.INVISIBLE

        if (recipe != null) recipesAdapter.setResults(recipe)
    }

}