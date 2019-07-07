package com.pingwinek.jens.cookandbake

import org.junit.Test

import org.junit.Assert.*

class SingletonHolderTest {

    class SingletonTester private constructor(val testString: String) {

        companion object : SingletonHolder<SingletonTester, String>(::SingletonTester)
    }

    @Test
    fun getInstance() {
        assert(SingletonTester.getInstance("Test") === SingletonTester.getInstance("Test"))
        assert(SingletonTester.getInstance("Test1") === SingletonTester.getInstance("Test2"))
        assert(SingletonTester.getInstance("Test") is SingletonTester)
    }
}