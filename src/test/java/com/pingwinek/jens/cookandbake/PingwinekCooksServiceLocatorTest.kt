package com.pingwinek.jens.cookandbake

import com.pingwinek.jens.cookandbake.models.IngredientLocal
import org.junit.Test

class PingwinekCooksServiceLocatorTest {

    private val serviceLocator = PingwinekCooksServiceLocator()

    @Test
    fun testRegisterAndGetService() {
        val testService = ""
        serviceLocator.registerService(testService)
        assert(serviceLocator.hasService(String::class.java))
        assert(serviceLocator.getService(String::class.java) == testService)

        val testIngredientLocal = IngredientLocal(1, 1, 1, null, null, "Test")
        serviceLocator.registerService(testIngredientLocal)
        assert(serviceLocator.hasService(IngredientLocal::class.java))
        assert(serviceLocator.getService(IngredientLocal::class.java) == testIngredientLocal)
    }
}