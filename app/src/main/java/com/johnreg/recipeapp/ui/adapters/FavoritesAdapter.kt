package com.johnreg.recipeapp.ui.adapters

import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.fragment.app.FragmentActivity
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.johnreg.recipeapp.R
import com.johnreg.recipeapp.data.entities.FavoriteEntity
import com.johnreg.recipeapp.ui.main.FavoritesFragmentDirections
import com.johnreg.recipeapp.utils.DiffUtilCallback
import com.johnreg.recipeapp.viewmodels.MainViewModel

class FavoritesAdapter(
    private val fragmentActivity: FragmentActivity,
    private val mainViewModel: MainViewModel
) : RecyclerView.Adapter<RecipesViewHolder>(), ActionMode.Callback {

    private var favorites: List<FavoriteEntity> = emptyList()

    private lateinit var rootView: View
    private lateinit var actionMode: ActionMode

    private var viewHolders: ArrayList<RecipesViewHolder> = arrayListOf()
    private var selectedItems: ArrayList<FavoriteEntity> = arrayListOf()

    private var isActionModeStarted = false

    override fun getItemCount(): Int {
        return favorites.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipesViewHolder {
        return RecipesViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: RecipesViewHolder, position: Int) {
        rootView = holder.itemView.rootView
        viewHolders.add(holder)

        val favoriteEntity = favorites[position]
        holder.bind(favoriteEntity.result)

        holder.itemView.setOnClickListener {
            if (!isActionModeStarted) {
                holder.itemView.findNavController().navigate(
                    FavoritesFragmentDirections.actionFavoritesFragmentToDetailsActivity(
                        favoriteEntity.result
                    )
                )
            } else {
                setSelectionAndTitle(holder, favoriteEntity)
            }
        }

        holder.itemView.setOnLongClickListener {
            if (!isActionModeStarted) {
                isActionModeStarted = true
                fragmentActivity.startActionMode(this)
            }
            setSelectionAndTitle(holder, favoriteEntity)
            true
        }

        if (selectedItems.contains(favoriteEntity)) {
            holder.setCardColors(R.color.colorPrimaryLighter, R.color.colorPrimary)
        } else {
            holder.setCardColors(R.color.white, R.color.lightMediumGray)
        }
    }

    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        actionMode = mode!!
        actionMode.menuInflater.inflate(R.menu.favorites_contextual_menu, menu)
        setStatusBarColor(R.color.darker)
        return true
    }

    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        return false
    }

    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.menu_delete_favorites -> {
                for (favoriteEntity in selectedItems) mainViewModel.deleteFavorite(favoriteEntity)

                Snackbar.make(
                    rootView, "${selectedItems.size} Recipe/s Removed.", Snackbar.LENGTH_SHORT
                ).setAction("Okay") {}.show()

                actionMode.finish()
                true
            }

            else -> false
        }
    }

    override fun onDestroyActionMode(mode: ActionMode?) {
        for (holder in viewHolders) holder.setCardColors(R.color.white, R.color.lightMediumGray)
        selectedItems.clear()
        isActionModeStarted = false
        setStatusBarColor(R.color.colorPrimaryDark)
    }

    fun setFavorites(favoriteEntityList: List<FavoriteEntity>) {
        val diffUtilCallback = DiffUtilCallback(favorites, favoriteEntityList)
        val diffUtilResult = DiffUtil.calculateDiff(diffUtilCallback)
        favorites = favoriteEntityList
        diffUtilResult.dispatchUpdatesTo(this)
    }

    fun finishActionMode() {
        if (this::actionMode.isInitialized) actionMode.finish()
    }

    private fun setSelectionAndTitle(holder: RecipesViewHolder, favoriteEntity: FavoriteEntity) {
        if (selectedItems.contains(favoriteEntity)) {
            holder.setCardColors(R.color.white, R.color.lightMediumGray)
            selectedItems.remove(favoriteEntity)
        } else {
            holder.setCardColors(R.color.colorPrimaryLighter, R.color.colorPrimary)
            selectedItems.add(favoriteEntity)
        }

        when (selectedItems.size) {
            0 -> actionMode.finish()
            1 -> actionMode.title = "1 item selected"
            else -> actionMode.title = "${selectedItems.size} items selected"
        }
    }

    private fun setStatusBarColor(@ColorRes statusBarColor: Int) {
        @Suppress("DEPRECATION")
        fragmentActivity.window.statusBarColor = fragmentActivity.getColor(statusBarColor)
    }

}