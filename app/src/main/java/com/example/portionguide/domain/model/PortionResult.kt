package com.example.portionguide.domain.model

import java.math.BigDecimal

data class PortionResult(
    val id: Long = 0L,
    val foodName: String = "",
    val mode: CalculationMode,
    val totalQuantity: BigDecimal,
    val unit: MeasurementUnit,
    val customUnitName: String = "",
    val portionCount: Int,
    val quantityPerPortion: BigDecimal,
    val remainingQuantity: BigDecimal,
    val totalDistributed: BigDecimal,
    val numberOfPeople: Int = 1,
    val formattedTotal: String,
    val formattedPerPortion: String,
    val formattedRemaining: String,
    val formattedDistributed: String,
    val portions: List<PortionItem> = emptyList(),
    val timestamp: Long = System.currentTimeMillis()
) {
    val displayUnit: String
        get() = if (unit == MeasurementUnit.CUSTOM) {
            if (customUnitName.isNotBlank()) customUnitName else "unit"
        } else {
            unit.symbol
        }

    val hasLeftover: Boolean
        get() = remainingQuantity.compareTo(BigDecimal.ZERO) > 0

    val distributedPercentage: Float
        get() = if (totalQuantity.compareTo(BigDecimal.ZERO) > 0) {
            (totalDistributed.toDouble() / totalQuantity.toDouble()).coerceIn(0.0, 1.0).toFloat()
        } else 0f

    val leftoverPercentage: Float
        get() = if (totalQuantity.compareTo(BigDecimal.ZERO) > 0) {
            (remainingQuantity.toDouble() / totalQuantity.toDouble()).coerceIn(0.0, 1.0).toFloat()
        } else 0f
}
