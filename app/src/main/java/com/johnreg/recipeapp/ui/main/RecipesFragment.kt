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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.johnreg.recipeapp.R
import com.johnreg.recipeapp.databinding.FragmentRecipesBinding
import com.johnreg.recipeapp.models.Recipe
import com.johnreg.recipeapp.ui.adapters.RecipesAdapter
import com.johnreg.recipeapp.utils.NetworkResult
import com.johnreg.recipeapp.utils.observeOnce
import com.johnreg.recipeapp.utils.setErrorTextAndListener
import com.johnreg.recipeapp.utils.showToast
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

    private var isNetworkObserved = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecipesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setMenu()
        showNetworkToast()
        setRvAndFab()
        checkDatabase()
    }

    override fun onPause() {
        super.onPause()
        isNetworkObserved = false
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

                    private fun searchApiData(query: String) {
                        mainViewModel.searchResponse.observe(viewLifecycleOwner) { response ->
                            handleRecipeResponse(response)
                        }
                        mainViewModel.searchRecipe(recipeViewModel.searchQueryMap(query))
                    }
                })
            }
        }, viewLifecycleOwner, Lifecycle.State.STARTED)
    }

    private fun showNetworkToast() {
        recipeViewModel.isNetworkAvailable.observe(viewLifecycleOwner) { isNetworkAvailable ->
            if (isNetworkAvailable && isNetworkObserved) showToast("We're Back Online.")
            if (!isNetworkAvailable) showToast("No Internet Connection.")

            Log.d("NetworkCallback", "Available: $isNetworkAvailable | Observe: $isNetworkObserved")
            isNetworkObserved = true
        }
    }

    private fun setRvAndFab() {
        binding.rvRecipes.apply {
            adapter = recipesAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        binding.fabRecipes.setOnClickListener {
            recipeViewModel.isNetworkAvailable.observeOnce(
                viewLifecycleOwner
            ) { isNetworkAvailable ->
                if (isNetworkAvailable) {
                    findNavController().navigate(R.id.action_recipesFragment_to_recipesBottomSheet)
                } else {
                    showToast("No Internet Connection.")
                }
            }
        }
    }

    private fun checkDatabase() {
        lifecycleScope.launch {
            mainViewModel.recipes.observeOnce(viewLifecycleOwner) { database ->
                if (database.isEmpty() || args.isApplyButtonClicked) requestApiData()
                else setRecipesAdapter(database.first().recipe)
            }
        }
    }

    private fun requestApiData() {
        mainViewModel.recipeResponse.observe(viewLifecycleOwner) { response ->
            handleRecipeResponse(response)
        }

        recipeViewModel.types.observe(viewLifecycleOwner) { types ->
            Log.d("RecipesFragment", types.toString())
            val queryMap = recipeViewModel.getQueryMap(types.mealTypeName, types.dietTypeName)
            mainViewModel.getRecipe(queryMap)
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

                binding.tvError.setErrorTextAndListener(response.message) { view ->
                    mainViewModel.recipes.observeOnce(viewLifecycleOwner) { database ->
                        if (database.isEmpty()) view.text = getString(R.string.cache_is_empty)
                        else setRecipesAdapter(database.first().recipe)
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