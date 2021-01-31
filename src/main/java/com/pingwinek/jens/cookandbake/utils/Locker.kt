package com.pingwinek.jens.cookandbake.utils

import java.util.*
import java.util.concurrent.ConcurrentHashMap

class Locker {
/*
    private val lockedInstances = ConcurrentHashMap<Int, Long>()

    @Synchronized
    fun lock(instanceId: Int) : Boolean {
        return if (lockedInstances.containsKey(instanceId)) {
            false
        } else {
            lockedInstances[instanceId] = Date().time
            true
        }
    }

    fun unlock(instanceId: Int) : Boolean {
        return if (lockedInstances.containsKey(instanceId)) {
            lockedInstances.remove(instanceId)
            true
        } else {
            false
        }
    }
*/
}