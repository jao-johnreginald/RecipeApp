package com.johnreg.recipeapp.ui.details

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.MenuProvider
import androidx.navigation.navArgs
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import com.johnreg.recipeapp.R
import com.johnreg.recipeapp.data.entities.FavoriteEntity
import com.johnreg.recipeapp.databinding.ActivityDetailsBinding
import com.johnreg.recipeapp.ui.adapters.PagerAdapter
import com.johnreg.recipeapp.utils.Constants.RESULT_BUNDLE_KEY
import com.johnreg.recipeapp.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailsBinding

    private lateinit var menuItem: MenuItem

    private val mainViewModel: MainViewModel by viewModels()
    private val args: DetailsActivityArgs by navArgs()

    private var favoriteId = 0
    private var isFavorite = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setMenu()
        setToolbar()
        setViewPager2AndTabLayout()
    }

    private fun setMenu() {
        addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.details_menu, menu)
                menuItem = menu.findItem(R.id.menu_favorite)
                observeFavorites()
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.menu_favorite -> {
                        if (isFavorite) {
                            mainViewModel.deleteFavorite(FavoriteEntity(args.result, favoriteId))
                            showSnackbar("Removed from Favorites.")
                        } else {
                            mainViewModel.insertFavorite(FavoriteEntity(args.result))
                            showSnackbar("Saved to Favorites.")
                        }
                        true
                    }

                    else -> false
                }
            }
        })
    }

    private fun observeFavorites() {
        mainViewModel.favorites.observe(this) { favoriteEntityList ->
            try {
                for (favoriteEntity in favoriteEntityList) {
                    if (favoriteEntity.result.id == args.result.id) {
                        menuItem.icon!!.setTint(getColor(R.color.yellow))
                        favoriteId = favoriteEntity.id
                        isFavorite = true
                        // Exit the observer lambda since a match is found
                        return@observe
                    }
                }
                // Execute only if no match was found
                menuItem.icon!!.setTint(getColor(R.color.white))
                isFavorite = false
            } catch (e: Exception) {
                Log.e("DetailsActivity", e.localizedMessage, e)
            }
        }
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).setAction("Okay") {}.show()
    }

    private fun setToolbar() {
        binding.toolbar.setTitleTextColor(getColor(R.color.white))
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setViewPager2AndTabLayout() {
        val fragments = arrayListOf(
            OverviewFragment(),
            IngredientsFragment(),
            InstructionsFragment()
        )

        val titles = arrayListOf(
            getString(R.string.overview),
            getString(R.string.ingredients),
            getString(R.string.instructions)
        )

        val resultBundle = bundleOf(
            RESULT_BUNDLE_KEY to args.result
        )

        binding.viewPager2.adapter = PagerAdapter(resultBundle, fragments, this)

        TabLayoutMediator(binding.tabLayout, binding.viewPager2) { tab, position ->
            tab.text = titles[position]
        }.attach()
    }

}