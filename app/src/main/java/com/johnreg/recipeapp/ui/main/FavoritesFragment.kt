package com.johnreg.recipeapp.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.johnreg.recipeapp.R
import com.johnreg.recipeapp.databinding.FragmentFavoritesBinding
import com.johnreg.recipeapp.ui.adapters.FavoritesAdapter
import com.johnreg.recipeapp.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    private var _favoritesAdapter: FavoritesAdapter? = null
    private val favoritesAdapter get() = _favoritesAdapter!!

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _favoritesAdapter = FavoritesAdapter(requireActivity(), mainViewModel)

        setMenu()
        setRecyclerView()
        observeFavorites()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        favoritesAdapter.finishActionMode()
        _binding = null
        _favoritesAdapter = null
    }

    private fun setMenu() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.favorites_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.menu_delete_all -> {
                        mainViewModel.deleteAllFavorites()

                        Snackbar.make(
                            requireView(), "All Recipes Removed.", Snackbar.LENGTH_SHORT
                        ).setAction("Okay") {}.show()

                        true
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.STARTED)
    }

    private fun setRecyclerView() {
        binding.rvFavorites.apply {
            adapter = favoritesAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun observeFavorites() {
        mainViewModel.favorites.observe(viewLifecycleOwner) { favoriteEntityList ->
            if (favoriteEntityList.isEmpty()) {
                binding.ivNoData.visibility = View.VISIBLE
                binding.tvNoData.visibility = View.VISIBLE
            } else {
                binding.ivNoData.visibility = View.INVISIBLE
                binding.tvNoData.visibility = View.INVISIBLE
            }

            favoritesAdapter.setFavorites(favoriteEntityList)
        }
    }

}