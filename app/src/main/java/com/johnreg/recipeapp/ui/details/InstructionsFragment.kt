package com.johnreg.recipeapp.ui.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import com.johnreg.recipeapp.data.models.Result
import com.johnreg.recipeapp.databinding.FragmentInstructionsBinding
import com.johnreg.recipeapp.utils.Constants.RESULT_BUNDLE_KEY
import com.johnreg.recipeapp.utils.getParcelableExtra

class InstructionsFragment : Fragment() {

    private var _binding: FragmentInstructionsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInstructionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getResultBundleAndSetUI()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getResultBundleAndSetUI() {
        val resultBundle = requireArguments().getParcelableExtra(RESULT_BUNDLE_KEY) as? Result

        resultBundle?.let { result ->
            binding.wvInstructions.apply {
                webViewClient = WebViewClient()
                loadUrl(result.sourceUrl)
            }
        }
    }

}