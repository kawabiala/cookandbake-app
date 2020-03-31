package com.pingwinek.jens.cookandbake.utils

import com.pingwinek.jens.cookandbake.utils.Locker
import org.junit.Assert.*

class LockerTest {

    @org.junit.Test
    fun lock() {
        val locker = Locker()

        // Can't unlock, when instance not existent
        assertFalse(locker.unlock(1))

        // Can lock new instanceId
        assertTrue(locker.lock(1))

        // Can lock another new instanceId
        assertTrue(locker.lock(2))

        // Can't lock already locked instanceId
        assertFalse(locker.lock(1))

        // Can unlock locked instanceId
        assertTrue(locker.unlock(1))

        // Can lock unlocked instanceId again
        assertTrue(locker.lock(1))
    }
}