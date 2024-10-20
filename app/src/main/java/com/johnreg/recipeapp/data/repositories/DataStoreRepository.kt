package com.johnreg.recipeapp.data.repositories

import android.content.Context
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.johnreg.recipeapp.utils.Constants.DEFAULT_DIET_TYPE
import com.johnreg.recipeapp.utils.Constants.DEFAULT_MEAL_TYPE
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@ActivityRetainedScoped
class DataStoreRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val Context.dataStore by preferencesDataStore("types_preferences")

    private object Keys {
        val mealTypeName = stringPreferencesKey("meal_type_name")
        val mealTypeId = intPreferencesKey("meal_type_id")
        val dietTypeName = stringPreferencesKey("diet_type_name")
        val dietTypeId = intPreferencesKey("diet_type_id")
    }

    suspend fun setTypesPreferences(
        mealTypeName: String,
        mealTypeId: Int,
        dietTypeName: String,
        dietTypeId: Int
    ) {
        context.dataStore.edit { preferences ->
            preferences[Keys.mealTypeName] = mealTypeName
            preferences[Keys.mealTypeId] = mealTypeId
            preferences[Keys.dietTypeName] = dietTypeName
            preferences[Keys.dietTypeId] = dietTypeId
        }
    }

    fun getTypesPreferences(): Flow<Types> {
        return context.dataStore.data
            .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
            .map { preferences ->
                Types(
                    mealTypeName = preferences[Keys.mealTypeName] ?: DEFAULT_MEAL_TYPE,
                    mealTypeId = preferences[Keys.mealTypeId] ?: 0,
                    dietTypeName = preferences[Keys.dietTypeName] ?: DEFAULT_DIET_TYPE,
                    dietTypeId = preferences[Keys.dietTypeId] ?: 0
                )
            }
    }

    data class Types(
        val mealTypeName: String,
        val mealTypeId: Int,
        val dietTypeName: String,
        val dietTypeId: Int
    )

}