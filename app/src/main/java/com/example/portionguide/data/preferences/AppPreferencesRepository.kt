package com.example.portionguide.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.portionguide.domain.model.MeasurementUnit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "portion_guide_preferences")

class AppPreferencesRepository(private val context: Context) {

    private object PreferencesKeys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val DEFAULT_UNIT = stringPreferencesKey("default_unit")
        val DECIMAL_PRECISION = intPreferencesKey("decimal_precision")
        val HAPTIC_FEEDBACK = booleanPreferencesKey("haptic_feedback")
    }

    val preferencesFlow: Flow<AppPreferences> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val themeString = preferences[PreferencesKeys.THEME_MODE] ?: ThemeMode.SYSTEM.name
            val themeMode = try {
                ThemeMode.valueOf(themeString)
            } catch (_: Exception) {
                ThemeMode.SYSTEM
            }

            val unitSymbol = preferences[PreferencesKeys.DEFAULT_UNIT] ?: MeasurementUnit.G.symbol
            val defaultUnit = MeasurementUnit.fromSymbol(unitSymbol)

            val precision = preferences[PreferencesKeys.DECIMAL_PRECISION] ?: 2
            val haptic = preferences[PreferencesKeys.HAPTIC_FEEDBACK] ?: true

            AppPreferences(
                themeMode = themeMode,
                defaultUnit = defaultUnit,
                decimalPrecision = precision.coerceIn(0, 3),
                hapticFeedbackEnabled = haptic
            )
        }

    suspend fun updateThemeMode(mode: ThemeMode) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME_MODE] = mode.name
        }
    }

    suspend fun updateDefaultUnit(unit: MeasurementUnit) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DEFAULT_UNIT] = unit.symbol
        }
    }

    suspend fun updateDecimalPrecision(precision: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DECIMAL_PRECISION] = precision.coerceIn(0, 3)
        }
    }

    suspend fun updateHapticFeedback(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.HAPTIC_FEEDBACK] = enabled
        }
    }
}
