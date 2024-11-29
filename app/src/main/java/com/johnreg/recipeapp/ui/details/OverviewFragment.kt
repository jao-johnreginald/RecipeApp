package com.johnreg.recipeapp.ui.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.getColor
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import coil.load
import com.johnreg.recipeapp.R
import com.johnreg.recipeapp.databinding.FragmentOverviewBinding
import com.johnreg.recipeapp.models.Result
import com.johnreg.recipeapp.utils.Constants.DURATION_MILLIS
import com.johnreg.recipeapp.utils.Constants.RESULT_BUNDLE_KEY
import com.johnreg.recipeapp.utils.getParcelableExtra
import org.jsoup.Jsoup

class OverviewFragment : Fragment() {

    private lateinit var binding: FragmentOverviewBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentOverviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getResultBundleAndSetUI()
    }

    private fun getResultBundleAndSetUI() {
        val resultBundle = requireArguments().getParcelableExtra(RESULT_BUNDLE_KEY) as? Result

        resultBundle?.let { result ->
            binding.ivMain.load(result.imageUrl) {
                crossfade(DURATION_MILLIS)
                error(R.drawable.ic_error)
            }

            binding.tvLikes.text = result.totalLikes.toString()
            binding.tvTime.text = result.totalMinutes.toString()
            binding.tvTitle.text = result.title
            binding.tvDescription.text = HtmlCompat.fromHtml(
                Jsoup.parse(result.description).html(), HtmlCompat.FROM_HTML_MODE_LEGACY
            )

            setColor(result.isVegetarian, binding.ivVegetarian, binding.tvVegetarian)
            setColor(result.isGlutenFree, binding.ivGlutenFree, binding.tvGlutenFree)
            setColor(result.isHealthy, binding.ivHealthy, binding.tvHealthy)
            setColor(result.isVegan, binding.ivVegan, binding.tvVegan)
            setColor(result.isDairyFree, binding.ivDairyFree, binding.tvDairyFree)
            setColor(result.isCheap, binding.ivCheap, binding.tvCheap)
        }
    }

    private fun setColor(isBooleanTrue: Boolean, imageView: ImageView, textView: TextView) {
        if (isBooleanTrue) {
            imageView.setColorFilter(getColor(requireContext(), R.color.green))
            textView.setTextColor(getColor(requireContext(), R.color.green))
        }
    }

}