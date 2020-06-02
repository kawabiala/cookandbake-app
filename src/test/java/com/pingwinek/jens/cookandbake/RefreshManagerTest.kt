package com.pingwinek.jens.cookandbake

import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.util.concurrent.atomic.AtomicInteger

class RefreshManagerTest {

    private val refresh = object : RefreshManager.Refresh {

        private var counter: AtomicInteger = AtomicInteger(0)

        override fun doRefresh(callback: (code: Int, response: String) -> Unit) {
            GlobalScope.launch {
                delay(100)
                callback(200, "refreshed ${counter.incrementAndGet()}")
            }
        }
    }

    private val refreshManager = RefreshManager(refresh)

    @Before
    fun setup() {
    }

    @Test
    fun refreshManagerTest() {
        refreshManager.refresh {
            assert(it)
        }
        refreshManager.refresh {
            assert(it)
        }
        Thread.sleep(200)
        refreshManager.refresh {
            assert(it)
        }
        Thread.sleep(200)
    }
}