package com.johnreg.recipeapp.ui.main

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.johnreg.recipeapp.R
import com.johnreg.recipeapp.data.viewmodels.RecipeViewModel
import com.johnreg.recipeapp.databinding.ActivityMainBinding
import com.johnreg.recipeapp.utils.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var navController: NavController

    private val recipeViewModel: RecipeViewModel by viewModels()

    private var isNetworkObserved = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        showNetworkToast()
        setNavController()
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    private fun showNetworkToast() {
        recipeViewModel.isNetworkAvailable.observe(this) { isNetworkAvailable ->
            Log.d("NetworkCallback", "Available: $isNetworkAvailable | Observe: $isNetworkObserved")
            if (isNetworkAvailable) {
                if (isNetworkObserved) showToast("We're Back Online.")
                isNetworkObserved = true
            } else {
                showToast("No Internet Connection.")
                isNetworkObserved = true
            }
        }
    }

    private fun setNavController() {
        navController = (supportFragmentManager.findFragmentById(
            R.id.fragmentContainerView
        ) as NavHostFragment).navController

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.recipesFragment,
                R.id.favoritesFragment,
                R.id.jokesFragment
            )
        )

        binding.bottomNavigationView.setupWithNavController(navController)
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

}