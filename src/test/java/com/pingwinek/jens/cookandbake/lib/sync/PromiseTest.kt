package com.pingwinek.jens.cookandbake.lib.sync

import org.junit.Test

class PromiseTest {

    @Test
    fun testResultFirst() {
        var called = false

        val promise = Promise<Any>()
        promise.setResult(Promise.Status.SUCCESS, null)
        assert(!called)

        promise.setResultHandler {
            called = true
        }
        assert(called)
    }

    @Test
    fun testHandlerFirst() {
        var called = false

        val promise = Promise<Any>()
        promise.setResultHandler {
            called = true
        }
        assert(!called)

        promise.setResult(Promise.Status.SUCCESS, null)
        assert(called)
    }
}