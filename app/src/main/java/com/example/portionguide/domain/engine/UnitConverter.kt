package com.example.portionguide.domain.engine

import com.example.portionguide.domain.model.MeasurementUnit
import java.math.BigDecimal
import java.math.RoundingMode

object UnitConverter {

    /**
     * Converts a value from sourceUnit to targetUnit if compatible.
     * Returns null if units cannot be converted directly (e.g., grams to milliliters without density, or piece to grams).
     */
    fun convert(
        value: BigDecimal,
        sourceUnit: MeasurementUnit,
        targetUnit: MeasurementUnit,
        scale: Int = 4
    ): BigDecimal? {
        if (sourceUnit == targetUnit) return value
        if (!sourceUnit.isCompatibleWith(targetUnit)) return null

        val sourceMultiplier = sourceUnit.baseMultiplier ?: return null
        val targetMultiplier = targetUnit.baseMultiplier ?: return null

        // Convert source -> base unit
        val inBaseUnits = value.multiply(sourceMultiplier)
        // Convert base unit -> target unit
        return inBaseUnits.divide(targetMultiplier, scale, RoundingMode.HALF_UP)
    }

    /**
     * Returns a list of compatible units for the given unit.
     */
    fun getCompatibleUnits(unit: MeasurementUnit): List<MeasurementUnit> {
        return MeasurementUnit.entries.filter { it.isCompatibleWith(unit) }
    }
}
