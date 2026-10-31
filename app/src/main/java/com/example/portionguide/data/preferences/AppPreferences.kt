package com.example.portionguide.data.preferences

import com.example.portionguide.domain.model.MeasurementUnit

enum class ThemeMode(val title: String) {
    SYSTEM("System Default"),
    LIGHT("Light Theme"),
    DARK("Dark Theme")
}

data class AppPreferences(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val defaultUnit: MeasurementUnit = MeasurementUnit.G,
    val decimalPrecision: Int = 2,
    val hapticFeedbackEnabled: Boolean = true
)
