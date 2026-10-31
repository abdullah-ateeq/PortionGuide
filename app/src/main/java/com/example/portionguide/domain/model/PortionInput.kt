package com.example.portionguide.domain.model

data class PortionInput(
    val foodName: String = "",
    val mode: CalculationMode = CalculationMode.BY_PORTIONS,
    val totalQuantityText: String = "",
    val unit: MeasurementUnit = MeasurementUnit.G,
    val customUnitName: String = "",
    val numberOfPortionsText: String = "4",
    val portionSizeText: String = "",
    val numberOfPeople: Int = 1
)
