package com.johnreg.recipeapp.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.johnreg.recipeapp.databinding.ItemIngredientBinding
import com.johnreg.recipeapp.data.models.Ingredient
import com.johnreg.recipeapp.utils.Constants.BASE_IMAGE_URL
import com.johnreg.recipeapp.utils.loadFrom

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
            binding.ivIngredient.loadFrom(BASE_IMAGE_URL + ingredient.imageUrl)

            binding.tvName.text = ingredient.name.replaceFirstChar { char ->
                if (char.isLowerCase()) char.titlecase() else char.toString()
            }

            binding.tvAmount.text = ingredient.amount.toString()
            binding.tvUnit.text = ingredient.unit
            binding.tvConsistency.text = ingredient.consistency
            binding.tvOriginal.text = ingredient.original
        }
    }

}