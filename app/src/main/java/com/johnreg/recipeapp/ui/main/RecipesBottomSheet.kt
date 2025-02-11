package com.johnreg.recipeapp.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.johnreg.recipeapp.data.viewmodels.RecipeViewModel
import com.johnreg.recipeapp.databinding.BottomSheetRecipesBinding
import com.johnreg.recipeapp.utils.Constants.DEFAULT_CHIP_ID
import com.johnreg.recipeapp.utils.Constants.DEFAULT_DIET_TYPE
import com.johnreg.recipeapp.utils.Constants.DEFAULT_MEAL_TYPE
import com.johnreg.recipeapp.utils.observeOnce
import com.johnreg.recipeapp.utils.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecipesBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetRecipesBinding? = null
    private val binding get() = _binding!!

    private val recipeViewModel: RecipeViewModel by viewModels()

    private var mealTypeName = DEFAULT_MEAL_TYPE
    private var dietTypeName = DEFAULT_DIET_TYPE

    private var mealTypeId = DEFAULT_CHIP_ID
    private var dietTypeId = DEFAULT_CHIP_ID

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetRecipesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setData()
        setListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setData() {
        recipeViewModel.types.observeOnce(viewLifecycleOwner) { types ->
            mealTypeName = types.mealTypeName
            dietTypeName = types.dietTypeName

            mealTypeId = types.mealTypeId
            dietTypeId = types.dietTypeId

            setChipChecked(mealTypeId, binding.cgMealType)
            setChipChecked(dietTypeId, binding.cgDietType)
        }
    }

    private fun setChipChecked(chipId: Int, chipGroup: ChipGroup) {
        if (chipId != DEFAULT_CHIP_ID) {
            try {
                val chip = chipGroup.findViewById<Chip>(chipId)
                chip.isChecked = true
                chipGroup.requestChildFocus(chip, chip)
            } catch (e: Exception) {
                Log.e("RecipesBottomSheet", e.localizedMessage, e)
                requireContext().showToast("Error: ${e.localizedMessage}")
            }
        }
    }

    private fun setListeners() {
        binding.cgMealType.setOnCheckedStateChangeListener { chipGroup, checkedIds ->
            val chipId = checkedIds.first()
            val chip = chipGroup.findViewById<Chip>(chipId)

            mealTypeName = chip.text.toString().lowercase()
            mealTypeId = chipId
            Log.d("RecipesBottomSheet", "Meal Type: ${mealTypeName.uppercase()} | Id: $mealTypeId")
        }

        binding.cgDietType.setOnCheckedStateChangeListener { chipGroup, checkedIds ->
            val chipId = checkedIds.first()
            val chip = chipGroup.findViewById<Chip>(chipId)

            dietTypeName = chip.text.toString().lowercase()
            dietTypeId = chipId
            Log.d("RecipesBottomSheet", "Diet Type: ${dietTypeName.uppercase()} | Id: $dietTypeId")
        }

        binding.btnApply.setOnClickListener {
            recipeViewModel.setTypes(mealTypeName, mealTypeId, dietTypeName, dietTypeId)

            findNavController().navigate(
                RecipesBottomSheetDirections.actionRecipesBottomSheetToRecipesFragment(true)
            )
        }
    }

}