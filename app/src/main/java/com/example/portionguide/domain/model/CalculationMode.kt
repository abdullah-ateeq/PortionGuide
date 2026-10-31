package com.example.portionguide.domain.model

enum class CalculationMode(
    val title: String,
    val description: String,
    val formulaDisplay: String
) {
    BY_PORTIONS(
        title = "Divide by Portions",
        description = "Split total food into a specific number of equal portions",
        formulaDisplay = "Quantity per Portion = Total Quantity ÷ Number of Portions"
    ),
    BY_SIZE(
        title = "Calculate Portions",
        description = "Determine how many portions of a specific size can be made",
        formulaDisplay = "Portions = floor(Total ÷ Portion Size), Remainder = Total − Distributed"
    )
}
