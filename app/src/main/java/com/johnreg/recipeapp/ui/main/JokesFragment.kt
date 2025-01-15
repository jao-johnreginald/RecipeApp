package com.johnreg.recipeapp.ui.main

import android.content.Intent
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
import com.johnreg.recipeapp.R
import com.johnreg.recipeapp.databinding.FragmentJokesBinding
import com.johnreg.recipeapp.utils.Constants.API_KEY
import com.johnreg.recipeapp.utils.NetworkResult
import com.johnreg.recipeapp.utils.observeOnce
import com.johnreg.recipeapp.utils.setErrorTextAndListener
import com.johnreg.recipeapp.data.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class JokesFragment : Fragment() {

    private lateinit var binding: FragmentJokesBinding

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentJokesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setMenu()
        checkDatabase()
    }

    private fun setMenu() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.jokes_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.menu_refresh -> {
                        requestApiData()
                        true
                    }

                    R.id.menu_share -> {
                        val shareIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, binding.tvJoke.text.toString())
                            type = "text/plain"
                        }
                        startActivity(shareIntent)
                        true
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.STARTED)
    }

    private fun checkDatabase() {
        mainViewModel.jokes.observeOnce(viewLifecycleOwner) { database ->
            if (database.isNotEmpty()) setJokeText(database.first().joke.text) else requestApiData()
        }
    }

    private fun requestApiData() {
        mainViewModel.jokeResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> setJokeText(response.data?.text)

                is NetworkResult.Error -> {
                    binding.progressBar.visibility = View.INVISIBLE
                    binding.cvJoke.visibility = View.INVISIBLE
                    binding.ivError.visibility = View.VISIBLE
                    binding.tvError.visibility = View.VISIBLE

                    binding.tvError.setErrorTextAndListener(response.message) { textView ->
                        mainViewModel.jokes.observeOnce(viewLifecycleOwner) { database ->
                            if (database.isNotEmpty()) setJokeText(database.first().joke.text)
                            else textView.text = getString(R.string.cache_is_empty)
                        }
                    }
                }

                is NetworkResult.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.cvJoke.visibility = View.INVISIBLE
                    binding.ivError.visibility = View.INVISIBLE
                    binding.tvError.visibility = View.INVISIBLE
                }
            }
        }

        mainViewModel.getJoke(API_KEY)
    }

    private fun setJokeText(text: String?) {
        binding.progressBar.visibility = View.INVISIBLE
        binding.cvJoke.visibility = View.VISIBLE
        binding.ivError.visibility = View.INVISIBLE
        binding.tvError.visibility = View.INVISIBLE

        binding.tvJoke.text = text
    }

}