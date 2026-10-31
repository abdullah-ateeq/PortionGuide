package com.example.portionguide.domain.model

import java.math.BigDecimal

enum class UnitCategory(val displayName: String) {
    WEIGHT("Weight"),
    VOLUME("Liquid Volume"),
    CULINARY("Kitchen Spoons & Cups"),
    COUNT("Pieces & Custom")
}

enum class MeasurementUnit(
    val symbol: String,
    val displayName: String,
    val category: UnitCategory,
    // Base conversion factor:
    // WEIGHT -> base unit is Gram (g)
    // VOLUME -> base unit is Milliliter (mL)
    // CULINARY -> base unit is Milliliter (mL)
    // COUNT -> discrete, no direct continuous base conversion
    val baseMultiplier: BigDecimal? = null
) {
    // Weight units (base: g)
    G("g", "Grams", UnitCategory.WEIGHT, BigDecimal("1.0")),
    KG("kg", "Kilograms", UnitCategory.WEIGHT, BigDecimal("1000.0")),
    MG("mg", "Milligrams", UnitCategory.WEIGHT, BigDecimal("0.001")),
    OZ("oz", "Ounces", UnitCategory.WEIGHT, BigDecimal("28.349523125")),
    LB("lb", "Pounds", UnitCategory.WEIGHT, BigDecimal("453.59237")),

    // Liquid Volume units (base: mL)
    ML("mL", "Milliliters", UnitCategory.VOLUME, BigDecimal("1.0")),
    L("L", "Liters", UnitCategory.VOLUME, BigDecimal("1000.0")),

    // Culinary Volume units (base: mL)
    CUP("cup", "Cups", UnitCategory.CULINARY, BigDecimal("240.0")),
    TBSP("tbsp", "Tablespoons", UnitCategory.CULINARY, BigDecimal("15.0")),
    TSP("tsp", "Teaspoons", UnitCategory.CULINARY, BigDecimal("5.0")),

    // Discrete / Count units
    PIECE("piece", "Pieces", UnitCategory.COUNT),
    SLICE("slice", "Slices", UnitCategory.COUNT),
    SERVING("serving", "Servings", UnitCategory.COUNT),
    CUSTOM("custom", "Custom Unit", UnitCategory.COUNT);

    fun format(quantity: String, customUnitName: String = ""): String {
        val unitLabel = if (this == CUSTOM) {
            if (customUnitName.isNotBlank()) customUnitName else "unit"
        } else {
            symbol
        }
        return "$quantity $unitLabel"
    }

    fun isCompatibleWith(other: MeasurementUnit): Boolean {
        if (this == other) return true
        if (this.category == UnitCategory.COUNT || other.category == UnitCategory.COUNT) {
            return false
        }
        // Weight can convert to Weight
        if (this.category == UnitCategory.WEIGHT && other.category == UnitCategory.WEIGHT) {
            return true
        }
        // Liquid volume and culinary volume are both volume based (mL)
        val isThisVolume = this.category == UnitCategory.VOLUME || this.category == UnitCategory.CULINARY
        val isOtherVolume = other.category == UnitCategory.VOLUME || other.category == UnitCategory.CULINARY
        return isThisVolume && isOtherVolume
    }

    companion object {
        fun fromSymbol(symbol: String): MeasurementUnit {
            return entries.firstOrNull { it.symbol.equals(symbol, ignoreCase = true) } ?: G
        }
    }
}
