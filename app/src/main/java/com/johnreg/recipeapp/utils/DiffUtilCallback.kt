package com.johnreg.recipeapp.utils

import androidx.recyclerview.widget.DiffUtil

class DiffUtilCallback(
    private val oldList: List<Any>,
    private val newList: List<Any>
) : DiffUtil.Callback() {

    // Returns the size of the old list
    override fun getOldListSize(): Int = oldList.size

    // Returns the size of the new list
    override fun getNewListSize(): Int = newList.size

    // Decides whether the two objects represent the same item in the old and new list
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }

    // Checks whether the two items have the same data, called only if areItemsTheSame() returns true
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }

}