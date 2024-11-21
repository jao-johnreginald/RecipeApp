package com.johnreg.recipeapp.ui.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import com.johnreg.recipeapp.databinding.FragmentInstructionsBinding
import com.johnreg.recipeapp.models.Result
import com.johnreg.recipeapp.utils.Constants.RESULT_BUNDLE_KEY
import com.johnreg.recipeapp.utils.getParcelableExtra

class InstructionsFragment : Fragment() {

    private lateinit var binding: FragmentInstructionsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentInstructionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val resultBundle = requireArguments().getParcelableExtra(RESULT_BUNDLE_KEY) as? Result
        resultBundle?.let { setWebView(it) }
    }

    private fun setWebView(result: Result) {
        binding.wvInstructions.apply {
            webViewClient = object : WebViewClient() {}
            loadUrl(result.sourceUrl)
        }
    }

}