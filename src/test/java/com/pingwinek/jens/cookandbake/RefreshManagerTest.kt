package com.pingwinek.jens.cookandbake

import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

class RefreshManagerTest {

    private val refresh = object : RefreshManager.Refresh {

        //private var counter: AtomicInteger = AtomicInteger(0)
        private var result: Boolean = false
        private var lock = Mutex()

        override suspend fun doRefresh() : Boolean {
            delay(100)
            //counter.incrementAndGet()
            lock.withLock {
                result = !result
            }
            return result
        }
    }

    private val refreshManager = RefreshManager(refresh)

    @Before
    fun setup() {
    }

    @Test
    fun refreshManagerTest() {
        CoroutineScope(Dispatchers.Default).launch {
            val result1 = refreshManager.refresh()
            val result2 = refreshManager.refresh()
            assert(result1 == result2)
            delay(200)
            val result3 = refreshManager.refresh()
            assert(result2 != result3)
        }
    }
}