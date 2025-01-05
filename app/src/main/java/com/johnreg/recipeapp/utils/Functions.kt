package com.johnreg.recipeapp.utils

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ClickableSpan
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.text.method.LinkMovementMethodCompat
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

fun Context.showToast(text: String) {
    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
}

fun TextView.setErrorTextAndListener(errorText: String?, listener: (view: TextView) -> Unit) {
    val loadCacheText = resources.getString(R.string.load_cache)

    val fullText = "$errorText\n$loadCacheText"
    val spannableString = SpannableString(fullText)

    // Handle click on "Load Cache?"
    val clickableSpan = object : ClickableSpan() {
        override fun onClick(view: View) = listener(view as TextView)
    }

    val startIndex = fullText.indexOf(loadCacheText)
    val endIndex = startIndex + loadCacheText.length

    // Apply formatting to "Load Cache?"
    spannableString.setSpan(
        clickableSpan,
        startIndex,
        endIndex,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )

    // Set the formatted text to the TextView and make the links clickable
    text = spannableString
    movementMethod = LinkMovementMethodCompat.getInstance()
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