package com.example.portionguide.domain.model

import java.math.BigDecimal

data class PortionItem(
    val index: Int,
    val title: String,
    val quantity: BigDecimal,
    val formattedQuantity: String,
    val unitLabel: String,
    val percentageOfTotal: Float
)
