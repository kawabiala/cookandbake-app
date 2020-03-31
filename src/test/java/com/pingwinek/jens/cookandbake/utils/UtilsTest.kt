package com.pingwinek.jens.cookandbake.utils

import com.pingwinek.jens.cookandbake.Utils.quantityToDouble
import com.pingwinek.jens.cookandbake.Utils.quantityToString
import org.junit.Test

class UtilsTest {

    @Test
    fun quantityToStringTest() {
        assert(quantityToString(null) == "")
        assert(quantityToString(0.0) == "")
        assert(quantityToString(1.0) == "1")
        assert(quantityToString(0.5) == "0.5")
    }

    @Test
    fun quantityToDoubleTest() {
        assert(quantityToDouble(null) == 0.0)
        assert(quantityToDouble("") == 0.0)
        assert(quantityToDouble("1") == 1.0)
        assert(quantityToDouble("1.5") == 1.5)
    }
}