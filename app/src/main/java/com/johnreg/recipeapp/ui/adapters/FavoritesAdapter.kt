package com.johnreg.recipeapp.ui.adapters

import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.johnreg.recipeapp.data.entities.FavoriteEntity
import com.johnreg.recipeapp.ui.main.FavoritesFragmentDirections
import com.johnreg.recipeapp.utils.DiffUtilCallback

class FavoritesAdapter : RecyclerView.Adapter<RecipesViewHolder>() {

    private var favorites: List<FavoriteEntity> = emptyList()

    override fun getItemCount(): Int {
        return favorites.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipesViewHolder {
        return RecipesViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: RecipesViewHolder, position: Int) {
        holder.bind(favorites[position].result)

        holder.itemView.setOnClickListener {
            holder.itemView.findNavController().navigate(
                FavoritesFragmentDirections.actionFavoritesFragmentToDetailsActivity(
                    favorites[position].result
                )
            )
        }
    }

    fun setFavorites(favoriteEntityList: List<FavoriteEntity>) {
        val diffUtilCallback = DiffUtilCallback(favorites, favoriteEntityList)
        val diffUtilResult = DiffUtil.calculateDiff(diffUtilCallback)
        favorites = favoriteEntityList
        diffUtilResult.dispatchUpdatesTo(this)
    }

}