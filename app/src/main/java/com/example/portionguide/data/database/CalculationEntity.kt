package com.example.portionguide.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.portionguide.domain.engine.PortionCalculationEngine
import com.example.portionguide.domain.engine.QuantityFormatter
import com.example.portionguide.domain.model.CalculationMode
import com.example.portionguide.domain.model.MeasurementUnit
import com.example.portionguide.domain.model.PortionInput
import com.example.portionguide.domain.model.PortionResult
import java.math.BigDecimal

@Entity(tableName = "calculations")
data class CalculationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val foodName: String,
    val modeName: String,
    val totalQuantityString: String,
    val unitSymbol: String,
    val customUnitName: String,
    val portionCount: Int,
    val quantityPerPortionString: String,
    val remainingQuantityString: String,
    val totalDistributedString: String,
    val numberOfPeople: Int,
    val timestamp: Long
) {
    fun toDomain(precision: Int = 2): PortionResult {
        val mode = try {
            CalculationMode.valueOf(modeName)
        } catch (_: Exception) {
            CalculationMode.BY_PORTIONS
        }
        val unit = MeasurementUnit.fromSymbol(unitSymbol)
        val total = QuantityFormatter.parse(totalQuantityString) ?: BigDecimal.ZERO
        val perPortion = QuantityFormatter.parse(quantityPerPortionString) ?: BigDecimal.ZERO
        val remaining = QuantityFormatter.parse(remainingQuantityString) ?: BigDecimal.ZERO
        val distributed = QuantityFormatter.parse(totalDistributedString) ?: BigDecimal.ZERO

        // Recompute breakdown items for display
        val input = PortionInput(
            foodName = foodName,
            mode = mode,
            totalQuantityText = total.toPlainString(),
            unit = unit,
            customUnitName = customUnitName,
            numberOfPortionsText = portionCount.toString(),
            portionSizeText = perPortion.toPlainString(),
            numberOfPeople = numberOfPeople
        )
        val computed = PortionCalculationEngine.calculate(input, precision)

        return computed.copy(
            id = id,
            foodName = foodName,
            timestamp = timestamp
        )
    }

    companion object {
        fun fromDomain(result: PortionResult): CalculationEntity {
            return CalculationEntity(
                id = result.id,
                foodName = result.foodName,
                modeName = result.mode.name,
                totalQuantityString = result.totalQuantity.toPlainString(),
                unitSymbol = result.unit.symbol,
                customUnitName = result.customUnitName,
                portionCount = result.portionCount,
                quantityPerPortionString = result.quantityPerPortion.toPlainString(),
                remainingQuantityString = result.remainingQuantity.toPlainString(),
                totalDistributedString = result.totalDistributed.toPlainString(),
                numberOfPeople = result.numberOfPeople,
                timestamp = result.timestamp
            )
        }
    }
}
