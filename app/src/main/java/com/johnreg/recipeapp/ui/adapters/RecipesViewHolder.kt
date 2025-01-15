package com.johnreg.recipeapp.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.text.parseAsHtml
import androidx.recyclerview.widget.RecyclerView
import com.johnreg.recipeapp.R
import com.johnreg.recipeapp.databinding.ItemRecipeBinding
import com.johnreg.recipeapp.data.models.Result
import com.johnreg.recipeapp.utils.Constants.TOTAL_MAX
import com.johnreg.recipeapp.utils.loadFrom
import org.jsoup.Jsoup

class RecipesViewHolder(
    private val binding: ItemRecipeBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(result: Result) {
        binding.ivRecipe.loadFrom(result.imageUrl)
        binding.tvTitle.text = result.title
        binding.tvDescription.text = Jsoup.parse(result.description).html().parseAsHtml()

        binding.tvHeart.text = if (result.totalLikes > TOTAL_MAX) TOTAL_MAX.toString()
        else result.totalLikes.toString()

        binding.tvClock.text = if (result.totalMinutes > TOTAL_MAX) TOTAL_MAX.toString()
        else result.totalMinutes.toString()

        if (result.isVegan) {
            binding.ivLeaf.setColorFilter(ContextCompat.getColor(itemView.context, R.color.green))
            binding.tvLeaf.setTextColor(ContextCompat.getColor(itemView.context, R.color.green))
        }
    }

    fun setCardColors(@ColorRes backgroundColor: Int, @ColorRes strokeColor: Int) {
        binding.clRow.setBackgroundColor(ContextCompat.getColor(itemView.context, backgroundColor))
        binding.cvRow.strokeColor = ContextCompat.getColor(itemView.context, strokeColor)
    }

    companion object {
        fun from(parent: ViewGroup): RecipesViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemRecipeBinding.inflate(inflater, parent, false)
            return RecipesViewHolder(binding)
        }
    }

}