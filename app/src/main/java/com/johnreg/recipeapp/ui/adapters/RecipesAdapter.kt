package com.johnreg.recipeapp.ui.adapters

import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.johnreg.recipeapp.data.models.Recipe
import com.johnreg.recipeapp.data.models.Result
import com.johnreg.recipeapp.ui.main.RecipesFragmentDirections
import com.johnreg.recipeapp.utils.DiffUtilCallback

class RecipesAdapter : RecyclerView.Adapter<RecipesViewHolder>() {

    private var results: List<Result> = emptyList()

    override fun getItemCount(): Int {
        return results.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipesViewHolder {
        return RecipesViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: RecipesViewHolder, position: Int) {
        holder.bind(results[position])

        holder.itemView.setOnClickListener {
            holder.itemView.findNavController().navigate(
                RecipesFragmentDirections.actionRecipesFragmentToDetailsActivity(
                    results[position]
                )
            )
        }
    }

    fun setResults(recipe: Recipe) {
        val diffUtilCallback = DiffUtilCallback(results, recipe.results)
        val diffUtilResult = DiffUtil.calculateDiff(diffUtilCallback)
        results = recipe.results
        diffUtilResult.dispatchUpdatesTo(this)
    }

}