package com.pingwinek.jens.cookandbake.utils

object Utils {

    fun quantityToString(quantity: Double?) : String {
        return when (quantity) {
            null, 0.0 -> ""
            quantity.toInt().toDouble() -> quantity.toInt().toString()
            else -> quantity.toString()
        }
    }

    fun quantityToDouble(quantity: String?) : Double {
        return when (quantity) {
            null, "" -> 0.0
            else -> quantity.toDouble()
        }
    }
}