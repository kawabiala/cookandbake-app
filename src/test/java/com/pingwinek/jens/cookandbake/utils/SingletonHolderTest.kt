package com.pingwinek.jens.cookandbake.utils

import org.junit.Test

class SingletonHolderTest {

    class SingletonTester private constructor(val testString: String) {

        companion object : SingletonHolder<SingletonTester, String>(
            SingletonHolderTest::SingletonTester
        )
    }

    @Test
    fun getInstance() {
        assert(SingletonTester.getInstance("Test") === SingletonTester.getInstance("Test"))
        assert(SingletonTester.getInstance("Test1") === SingletonTester.getInstance("Test2"))
    }
}