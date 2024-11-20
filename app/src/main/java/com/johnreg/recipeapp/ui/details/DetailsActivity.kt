package com.johnreg.recipeapp.ui.details

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.navigation.navArgs
import com.google.android.material.tabs.TabLayoutMediator
import com.johnreg.recipeapp.R
import com.johnreg.recipeapp.databinding.ActivityDetailsBinding
import com.johnreg.recipeapp.ui.adapters.PagerAdapter
import com.johnreg.recipeapp.utils.Constants.RESULT_BUNDLE_KEY

class DetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailsBinding

    private val args: DetailsActivityArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setToolbar()
        setViewPager2AndTabLayout()
    }

    private fun setToolbar() {
        binding.toolbar.setTitleTextColor(getColor(R.color.white))
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setViewPager2AndTabLayout() {
        val fragments = arrayListOf(
            OverviewFragment(),
            IngredientsFragment(),
            InstructionsFragment()
        )

        val titles = arrayListOf(
            getString(R.string.overview),
            getString(R.string.ingredients),
            getString(R.string.instructions)
        )

        val resultBundle = bundleOf(
            RESULT_BUNDLE_KEY to args.result
        )

        binding.viewPager2.adapter = PagerAdapter(resultBundle, fragments, this)

        TabLayoutMediator(binding.tabLayout, binding.viewPager2) { tab, position ->
            tab.text = titles[position]
        }.attach()
    }

}