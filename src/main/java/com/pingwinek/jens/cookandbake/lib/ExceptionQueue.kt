package com.pingwinek.jens.cookandbake.lib

class ExceptionQueue {

    interface ExceptionListener {
        fun nexException()
    }

    private val queue = mutableListOf<Exception>()
    private val listeners = mutableListOf<ExceptionListener>()

    fun addException(exception: java.lang.Exception) {
        queue.add(exception)
        listeners.forEach { listener ->
            listener.nexException()
        }
    }

    fun getLatest(): Exception? {
        val exception = if (queue.isEmpty()) null else queue.last()
        queue.clear()
        return exception
    }

    fun getOldest(): Exception? {
        return if (queue.isEmpty()) null else queue.removeFirst()
    }

    fun registerListener(listener: ExceptionListener) {
        listeners.add(listener)
    }

    fun unregisterListener(listener: ExceptionListener) {
        listeners.remove(listener)
    }
}