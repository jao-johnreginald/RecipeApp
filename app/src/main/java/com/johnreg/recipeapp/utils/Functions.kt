package com.johnreg.recipeapp.utils

import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import coil.load
import com.johnreg.recipeapp.R
import com.johnreg.recipeapp.utils.Constants.DURATION_MILLIS

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

/**
 * Use the coil image loading library to load this image from the url into our ImageView.
 *
 * When our image is loading or when it is loaded, it will have a fade in animation of
 * (constant's value) milliseconds, we will see that effect when we start fetching some api data.
 *
 * The images that were not cached correctly will display this error icon.
 */
fun ImageView.loadFrom(imageUrl: String) = load(imageUrl) {
    crossfade(DURATION_MILLIS)
    error(R.drawable.ic_error)
}