package com.pingwinek.jens.cookandbake.utils

class CallbackLoopCounter(private val onAllEnded: () -> Unit) {

    private var counter = 0
    private var ended = false

    fun taskStarted() {
        counter++
    }

    fun taskEnded() {
        counter--
        if (counter == 0 && !ended) {
            onAllEnded()
        }
        if (counter <= 0) {
            ended = true
        }
    }
}