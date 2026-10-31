package com.example.portionguide.domain.engine

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

object QuantityFormatter {

    fun format(value: BigDecimal, precision: Int = 2): String {
        val clampedPrecision = precision.coerceIn(0, 4)
        val pattern = buildString {
            append("#,##0")
            if (clampedPrecision > 0) {
                append(".")
                repeat(clampedPrecision) { append("#") }
            }
        }
        val symbols = DecimalFormatSymbols(Locale.US)
        val df = DecimalFormat(pattern, symbols)
        val rounded = value.setScale(clampedPrecision, RoundingMode.HALF_UP)
        return df.format(rounded)
    }

    fun parse(text: String): BigDecimal? {
        val clean = text.trim().replace(",", ".")
        if (clean.isBlank()) return null
        return try {
            val bd = BigDecimal(clean)
            if (bd.compareTo(BigDecimal.ZERO) >= 0) bd else null
        } catch (_: Exception) {
            null
        }
    }
}
