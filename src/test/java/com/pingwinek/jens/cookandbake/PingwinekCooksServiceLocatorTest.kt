package com.pingwinek.jens.cookandbake

import org.junit.Test

class PingwinekCooksServiceLocatorTest {

    private val serviceLocator = PingwinekCooksServiceLocator()

    @Test
    fun testRegisterAndGetService() {
        val testService = ""
        serviceLocator.registerService(testService)
        assert(serviceLocator.hasService(String::class.java))
        assert(serviceLocator.getService(String::class.java) == testService)
    }
}