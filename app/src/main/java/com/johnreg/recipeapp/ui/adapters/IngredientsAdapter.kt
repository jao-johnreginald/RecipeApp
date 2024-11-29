package com.johnreg.recipeapp.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.johnreg.recipeapp.R
import com.johnreg.recipeapp.databinding.ItemIngredientBinding
import com.johnreg.recipeapp.models.Ingredient
import com.johnreg.recipeapp.utils.Constants.BASE_IMAGE_URL
import com.johnreg.recipeapp.utils.Constants.DURATION_MILLIS
import java.util.Locale

class IngredientsAdapter(
    private val ingredients: List<Ingredient>
) : RecyclerView.Adapter<IngredientsAdapter.IngredientsViewHolder>() {

    override fun getItemCount(): Int = ingredients.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemIngredientBinding.inflate(inflater, parent, false)
        return IngredientsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: IngredientsViewHolder, position: Int) {
        holder.bind(ingredients[position])
    }

    inner class IngredientsViewHolder(
        private val binding: ItemIngredientBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(ingredient: Ingredient) {
            binding.ivIngredient.load(BASE_IMAGE_URL + ingredient.imageUrl) {
                crossfade(DURATION_MILLIS)
                error(R.drawable.ic_error)
            }

            binding.tvName.text = ingredient.name.replaceFirstChar { char ->
                if (char.isLowerCase()) char.titlecase(Locale.ROOT) else char.toString()
            }

            binding.tvAmount.text = String.format(Locale.ROOT, ingredient.amount.toString())
            binding.tvUnit.text = ingredient.unit
            binding.tvConsistency.text = ingredient.consistency
            binding.tvOriginal.text = ingredient.original
        }
    }

}