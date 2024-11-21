package com.johnreg.recipeapp.ui.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.johnreg.recipeapp.databinding.FragmentIngredientsBinding
import com.johnreg.recipeapp.models.Result
import com.johnreg.recipeapp.ui.adapters.IngredientsAdapter
import com.johnreg.recipeapp.utils.Constants.RESULT_BUNDLE_KEY
import com.johnreg.recipeapp.utils.getParcelableExtra

class IngredientsFragment : Fragment() {

    private lateinit var binding: FragmentIngredientsBinding

    private val ingredientsAdapter by lazy { IngredientsAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentIngredientsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setRecyclerView()
        getResultBundleAndSetUI()
    }

    private fun setRecyclerView() {
        binding.rvIngredients.apply {
            adapter = ingredientsAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun getResultBundleAndSetUI() {
        val resultBundle = requireArguments().getParcelableExtra(RESULT_BUNDLE_KEY) as? Result

        resultBundle?.let { result ->
            ingredientsAdapter.setIngredients(result.ingredients)
        }
    }

}