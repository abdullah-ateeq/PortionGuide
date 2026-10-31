package com.example.portionguide

import com.example.portionguide.domain.engine.PortionCalculationEngine
import com.example.portionguide.domain.engine.QuantityFormatter
import com.example.portionguide.domain.engine.UnitConverter
import com.example.portionguide.domain.model.CalculationMode
import com.example.portionguide.domain.model.MeasurementUnit
import com.example.portionguide.domain.model.PortionInput
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.math.BigDecimal

class PortionCalculationEngineTest {

    @Test
    fun testModeA_EqualDivisionNoRemainder() {
        // Total food: 1,200 g, 6 portions -> 200 g each, 0 remainder
        val input = PortionInput(
            foodName = "Chicken Stew",
            mode = CalculationMode.BY_PORTIONS,
            totalQuantityText = "1200",
            unit = MeasurementUnit.G,
            numberOfPortionsText = "6"
        )

        val result = PortionCalculationEngine.calculate(input, precision = 2)
        assertEquals(6, result.portionCount)
        assertEquals("200", result.formattedPerPortion)
        assertEquals("0", result.formattedRemaining)
        assertEquals("1,200", result.formattedDistributed)
        assertFalse(result.hasLeftover)
        assertEquals(6, result.portions.size)
    }

    @Test
    fun testModeA_EqualDivisionWithFractionalRemainder() {
        // Total food: 1,000 g, 3 portions -> 333.33 g each, 0.01 g remainder at 2 precision
        val input = PortionInput(
            foodName = "Rice",
            mode = CalculationMode.BY_PORTIONS,
            totalQuantityText = "1000",
            unit = MeasurementUnit.G,
            numberOfPortionsText = "3"
        )

        val result = PortionCalculationEngine.calculate(input, precision = 2)
        assertEquals(3, result.portionCount)
        assertEquals("333.33", result.formattedPerPortion)
        assertEquals("0.01", result.formattedRemaining)
        assertTrue(result.hasLeftover)
    }

    @Test
    fun testModeB_CalculateNumberOfPortions() {
        // Total: 1,000 g, Desired portion: 180 g -> 5 portions, distributed 900 g, remainder 100 g
        val input = PortionInput(
            foodName = "Beef Chili",
            mode = CalculationMode.BY_SIZE,
            totalQuantityText = "1000",
            unit = MeasurementUnit.G,
            portionSizeText = "180"
        )

        val result = PortionCalculationEngine.calculate(input, precision = 2)
        assertEquals(5, result.portionCount)
        assertEquals("180", result.formattedPerPortion)
        assertEquals("900", result.formattedDistributed)
        assertEquals("100", result.formattedRemaining)
        assertTrue(result.hasLeftover)
        assertEquals(5, result.portions.size)
    }

    @Test
    fun testInputValidation() {
        // Invalid empty input
        val emptyInput = PortionInput()
        val errorResult = PortionCalculationEngine.validateInput(emptyInput)
        assertTrue(errorResult is PortionCalculationEngine.ValidationResult.Error)

        // Custom unit without custom name
        val customMissingName = PortionInput(
            totalQuantityText = "500",
            unit = MeasurementUnit.CUSTOM,
            customUnitName = ""
        )
        val customError = PortionCalculationEngine.validateInput(customMissingName)
        assertTrue(customError is PortionCalculationEngine.ValidationResult.Error)

        // Valid custom unit
        val validCustom = PortionInput(
            totalQuantityText = "12",
            unit = MeasurementUnit.CUSTOM,
            customUnitName = "dumplings",
            numberOfPortionsText = "4"
        )
        val validResult = PortionCalculationEngine.validateInput(validCustom)
        assertTrue(validResult is PortionCalculationEngine.ValidationResult.Valid)
    }

    @Test
    fun testUnitConversion() {
        // 1 kg = 1000 g
        val grams = UnitConverter.convert(BigDecimal("1"), MeasurementUnit.KG, MeasurementUnit.G)
        assertNotNull(grams)
        assertEquals(0, grams!!.compareTo(BigDecimal("1000")))

        // 1 L = 1000 mL
        val ml = UnitConverter.convert(BigDecimal("2"), MeasurementUnit.L, MeasurementUnit.ML)
        assertNotNull(ml)
        assertEquals(0, ml!!.compareTo(BigDecimal("2000")))

        // 1 cup = 240 mL
        val cupInMl = UnitConverter.convert(BigDecimal("1"), MeasurementUnit.CUP, MeasurementUnit.ML)
        assertNotNull(cupInMl)
        assertEquals(0, cupInMl!!.compareTo(BigDecimal("240")))

        // Incompatible units: Grams to Liters should return null
        val incompatible = UnitConverter.convert(BigDecimal("500"), MeasurementUnit.G, MeasurementUnit.L)
        assertNull(incompatible)
    }

    @Test
    fun testQuantityFormatter() {
        assertEquals("1,250", QuantityFormatter.format(BigDecimal("1250"), 2))
        assertEquals("12.5", QuantityFormatter.format(BigDecimal("12.500"), 2))
        assertEquals("0.33", QuantityFormatter.format(BigDecimal("0.33333"), 2))
        assertEquals("0.333", QuantityFormatter.format(BigDecimal("0.33333"), 3))
    }
}
