package com.johnreg.recipeapp.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getColor
import androidx.core.text.HtmlCompat
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.johnreg.recipeapp.R
import com.johnreg.recipeapp.data.entities.FavoriteEntity
import com.johnreg.recipeapp.databinding.ItemRecipeBinding
import com.johnreg.recipeapp.models.Result
import com.johnreg.recipeapp.ui.main.FavoritesFragmentDirections
import com.johnreg.recipeapp.utils.Constants.DURATION_MILLIS
import com.johnreg.recipeapp.utils.Constants.TOTAL_MAX
import com.johnreg.recipeapp.utils.DiffUtilCallback
import org.jsoup.Jsoup

class FavoritesAdapter : RecyclerView.Adapter<FavoritesAdapter.FavoritesViewHolder>() {

    private var favorites: List<FavoriteEntity> = emptyList()

    override fun getItemCount(): Int = favorites.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoritesViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemRecipeBinding.inflate(inflater, parent, false)
        return FavoritesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoritesViewHolder, position: Int) {
        holder.bind(favorites[position].result)
    }

    fun setFavorites(favoriteEntityList: List<FavoriteEntity>) {
        val diffUtilCallback = DiffUtilCallback(favorites, favoriteEntityList)
        val diffUtilResult = DiffUtil.calculateDiff(diffUtilCallback)
        favorites = favoriteEntityList
        diffUtilResult.dispatchUpdatesTo(this)
    }

    inner class FavoritesViewHolder(
        private val binding: ItemRecipeBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(result: Result) {
            binding.ivRecipe.load(result.imageUrl) {
                crossfade(DURATION_MILLIS)
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
                binding.ivLeaf.setColorFilter(getColor(itemView.context, R.color.green))
                binding.tvLeaf.setTextColor(getColor(itemView.context, R.color.green))
            }

            itemView.setOnClickListener {
                itemView.findNavController().navigate(
                    FavoritesFragmentDirections.actionFavoritesFragmentToDetailsActivity(result)
                )
            }
        }
    }

}