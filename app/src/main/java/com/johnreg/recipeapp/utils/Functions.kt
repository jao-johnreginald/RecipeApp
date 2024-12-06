package com.johnreg.recipeapp.utils

import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
    observe(lifecycleOwner, object : Observer<T> {
        override fun onChanged(value: T) {
            removeObserver(this)
            observer.onChanged(value)
        }
    })
}

inline fun <reified T : Parcelable> Bundle.getParcelableExtra(key: String): T? = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getParcelable(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelable(key) as? T
}

fun Fragment.showToast(text: String) {
    Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
}