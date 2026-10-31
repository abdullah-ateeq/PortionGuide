package com.example.portionguide.domain.engine

import com.example.portionguide.domain.model.CalculationMode
import com.example.portionguide.domain.model.MeasurementUnit
import com.example.portionguide.domain.model.PortionInput
import com.example.portionguide.domain.model.PortionItem
import com.example.portionguide.domain.model.PortionResult
import java.math.BigDecimal
import java.math.RoundingMode

object PortionCalculationEngine {

    sealed class ValidationResult {
        object Valid : ValidationResult()
        data class Error(val message: String) : ValidationResult()
    }

    fun validateInput(input: PortionInput): ValidationResult {
        val total = QuantityFormatter.parse(input.totalQuantityText)
            ?: return ValidationResult.Error("Please enter a valid total quantity.")

        if (total.compareTo(BigDecimal.ZERO) <= 0) {
            return ValidationResult.Error("Total quantity must be greater than zero.")
        }

        if (input.unit == MeasurementUnit.CUSTOM && input.customUnitName.isBlank()) {
            return ValidationResult.Error("Please specify your custom unit name.")
        }

        when (input.mode) {
            CalculationMode.BY_PORTIONS -> {
                val portions = input.numberOfPortionsText.toIntOrNull()
                if (portions == null || portions <= 0) {
                    return ValidationResult.Error("Please enter at least 1 portion.")
                }
                if (portions > 1000) {
                    return ValidationResult.Error("Number of portions cannot exceed 1,000.")
                }
            }
            CalculationMode.BY_SIZE -> {
                val portionSize = QuantityFormatter.parse(input.portionSizeText)
                    ?: return ValidationResult.Error("Please enter a valid desired portion size.")
                if (portionSize.compareTo(BigDecimal.ZERO) <= 0) {
                    return ValidationResult.Error("Portion size must be greater than zero.")
                }
                if (portionSize > total) {
                    return ValidationResult.Error("Portion size cannot exceed the total quantity.")
                }
            }
        }

        return ValidationResult.Valid
    }

    fun calculate(
        input: PortionInput,
        precision: Int = 2
    ): PortionResult {
        val total = QuantityFormatter.parse(input.totalQuantityText) ?: BigDecimal.ZERO
        val unitLabel = if (input.unit == MeasurementUnit.CUSTOM) {
            if (input.customUnitName.isNotBlank()) input.customUnitName.trim() else "unit"
        } else {
            input.unit.symbol
        }

        val portionCount: Int
        val quantityPerPortion: BigDecimal
        val totalDistributed: BigDecimal
        val remainingQuantity: BigDecimal

        when (input.mode) {
            CalculationMode.BY_PORTIONS -> {
                val requestedPortions = (input.numberOfPortionsText.toIntOrNull() ?: 1).coerceAtLeast(1)
                portionCount = requestedPortions
                val portionsBd = BigDecimal(portionCount)

                // Divide total by portions with rounding
                val rawPerPortion = total.divide(portionsBd, precision + 2, RoundingMode.HALF_UP)
                quantityPerPortion = rawPerPortion.setScale(precision, RoundingMode.DOWN)

                // Total distributed based on the calculated portion size
                val computedDistributed = quantityPerPortion.multiply(portionsBd)
                val rawRemaining = total.subtract(computedDistributed)

                // Clean up small rounding artifacts if mathematically exact
                if (rawRemaining.abs() < BigDecimal("0.00001")) {
                    totalDistributed = total
                    remainingQuantity = BigDecimal.ZERO
                } else {
                    totalDistributed = computedDistributed
                    remainingQuantity = rawRemaining.max(BigDecimal.ZERO)
                }
            }

            CalculationMode.BY_SIZE -> {
                val desiredSize = QuantityFormatter.parse(input.portionSizeText) ?: BigDecimal.ONE
                val rawPortionCount = total.divide(desiredSize, 0, RoundingMode.FLOOR).toInt()
                portionCount = rawPortionCount.coerceAtLeast(0)
                quantityPerPortion = desiredSize

                totalDistributed = quantityPerPortion.multiply(BigDecimal(portionCount))
                remainingQuantity = total.subtract(totalDistributed).max(BigDecimal.ZERO)
            }
        }

        // Build individual portion breakdown cards (cap at 60 items for visual performance)
        val displayPortionLimit = 60
        val renderedCount = portionCount.coerceAtMost(displayPortionLimit)
        val portionItems = (1..renderedCount).map { index ->
            val fraction = if (total.compareTo(BigDecimal.ZERO) > 0) {
                (quantityPerPortion.toDouble() / total.toDouble()).coerceIn(0.0, 1.0).toFloat()
            } else 0f

            PortionItem(
                index = index,
                title = "Portion #$index",
                quantity = quantityPerPortion,
                formattedQuantity = QuantityFormatter.format(quantityPerPortion, precision),
                unitLabel = unitLabel,
                percentageOfTotal = fraction
            )
        }

        return PortionResult(
            foodName = input.foodName.trim(),
            mode = input.mode,
            totalQuantity = total,
            unit = input.unit,
            customUnitName = input.customUnitName.trim(),
            portionCount = portionCount,
            quantityPerPortion = quantityPerPortion,
            remainingQuantity = remainingQuantity,
            totalDistributed = totalDistributed,
            numberOfPeople = input.numberOfPeople.coerceAtLeast(1),
            formattedTotal = QuantityFormatter.format(total, precision),
            formattedPerPortion = QuantityFormatter.format(quantityPerPortion, precision),
            formattedRemaining = QuantityFormatter.format(remainingQuantity, precision),
            formattedDistributed = QuantityFormatter.format(totalDistributed, precision),
            portions = portionItems
        )
    }

    /**
     * Recalculate directly when the user increments or decrements portions on the result screen.
     */
    fun recalculateWithPortionCount(
        current: PortionResult,
        newPortionCount: Int,
        precision: Int = 2
    ): PortionResult {
        val clampedPortions = newPortionCount.coerceAtLeast(1)
        val input = PortionInput(
            foodName = current.foodName,
            mode = CalculationMode.BY_PORTIONS,
            totalQuantityText = current.totalQuantity.toPlainString(),
            unit = current.unit,
            customUnitName = current.customUnitName,
            numberOfPortionsText = clampedPortions.toString(),
            portionSizeText = "",
            numberOfPeople = current.numberOfPeople
        )
        return calculate(input, precision).copy(id = current.id, timestamp = current.timestamp)
    }
}
