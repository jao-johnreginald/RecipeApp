package com.johnreg.recipeapp.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.johnreg.recipeapp.R
import com.johnreg.recipeapp.databinding.ItemRecipeBinding
import com.johnreg.recipeapp.models.Result
import com.johnreg.recipeapp.utils.Constants.DURATION_MILLIS
import com.johnreg.recipeapp.utils.Constants.TOTAL_MAX
import org.jsoup.Jsoup

class RecipesViewHolder(
    private val binding: ItemRecipeBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(result: Result) {
        // Use the coil image loading library to load this image from the url into our ImageView
        binding.ivRecipe.load(result.imageUrl) {
            // When our image is loading or when it is loaded, it will have a fade in animation
            // of DURATION_MILLIS, we will see that effect when we start fetching some api data
            crossfade(DURATION_MILLIS)
            // The images that were not cached correctly will display this error icon
            error(R.drawable.ic_error)
        }

        binding.tvTitle.text = result.title
        binding.tvDescription.text = HtmlCompat.fromHtml(
            Jsoup.parse(result.description).html(), HtmlCompat.FROM_HTML_MODE_LEGACY
        )

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