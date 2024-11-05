package com.johnreg.recipeapp.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.core.view.MenuProvider
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
        checkDatabaseAndArgs()
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
        })
    }

    private fun setSearchView(menu: Menu) {
        val searchView = menu.findItem(R.id.menu_search).actionView as? SearchView
        searchView?.isSubmitButtonEnabled = true
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean = false
        })
    }

    private fun showNetworkToast() {
        recipeViewModel.isNetworkAvailable.observe(viewLifecycleOwner) { isNetworkAvailable ->
            if (!isNetworkAvailable) {
                Toast.makeText(
                    requireContext(), "No Internet Connection.", Toast.LENGTH_SHORT
                ).show()
            }

            if (isNetworkAvailable && isNetworkObserved) {
                Toast.makeText(requireContext(), "We're Back Online.", Toast.LENGTH_SHORT).show()
            }

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
                    Toast.makeText(
                        requireContext(), "No Internet Connection.", Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun checkDatabaseAndArgs() {
        lifecycleScope.launch {
            mainViewModel.recipes.observeOnce(viewLifecycleOwner) { database ->
                if (database.isEmpty() || args.isApplyButtonClicked) {
                    requestApiData()
                } else {
                    Log.d("RecipesFragment", "else block called")
                    val recipe = database.first().recipe
                    recipesAdapter.setResults(recipe)
                }
            }
        }
    }

    private fun requestApiData() {
        Log.d("RecipesFragment", "requestApiData() called")
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
                    binding.ivError.visibility = View.INVISIBLE
                    binding.tvError.visibility = View.INVISIBLE
                }
            }
        }

        recipeViewModel.types.observe(viewLifecycleOwner) { types ->
            Log.d("RecipesFragment", types.toString())
            val queryMap = recipeViewModel.getQueryMap(types.mealTypeName, types.dietTypeName)
            mainViewModel.getRecipe(queryMap)
        }
    }

}