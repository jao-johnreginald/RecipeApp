package com.johnreg.recipeapp.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getColor
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.johnreg.recipeapp.R
import com.johnreg.recipeapp.databinding.ItemRecipeBinding
import com.johnreg.recipeapp.models.Recipe
import com.johnreg.recipeapp.models.Result
import com.johnreg.recipeapp.utils.DiffUtilCallback

class RecipesAdapter : RecyclerView.Adapter<RecipesAdapter.RecipesViewHolder>() {

    private var results: List<Result> = emptyList()

    override fun getItemCount(): Int = results.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipesViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemRecipeBinding.inflate(inflater, parent, false)
        return RecipesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecipesViewHolder, position: Int) {
        holder.bind(results[position])
    }

    fun setResults(recipe: Recipe) {
        val diffUtilCallback = DiffUtilCallback(results, recipe.results)
        val diffUtilResult = DiffUtil.calculateDiff(diffUtilCallback)
        results = recipe.results
        diffUtilResult.dispatchUpdatesTo(this)
    }

    inner class RecipesViewHolder(
        private val binding: ItemRecipeBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(result: Result) {
            // Use the coil image loading library to load this image from the url into our ImageView
            binding.ivRecipe.load(result.imageUrl) {
                // When our image is loading or when it is loaded, it will have a fade in animation
                // of 600 milliseconds, we will see that effect when we start fetching some api data
                crossfade(600)
            }

            binding.tvTitle.text = result.title
            binding.tvDescription.text = result.description

            binding.tvHeart.text = result.totalLikes.toString()
            binding.tvClock.text = result.totalMinutes.toString()

            if (result.isVegan) {
                binding.ivLeaf.setColorFilter(getColor(itemView.context, R.color.green))
                binding.tvLeaf.setTextColor(getColor(itemView.context, R.color.green))
            }
        }
    }

}