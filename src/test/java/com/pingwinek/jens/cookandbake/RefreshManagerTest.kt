package com.pingwinek.jens.cookandbake

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.junit.Before
import org.junit.Test

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