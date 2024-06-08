package com.pingwinek.jens.cookandbake.lib

class TypedQueue<T> {

    interface QueueListener {
        fun onNewItem()
    }

    private val queue = mutableListOf<T>()
    private val listeners = mutableListOf<QueueListener>()

    fun addItem(item: T) {
        queue.add(item)
        listeners.forEach { listener ->
            listener.onNewItem()
        }
    }

    fun getLatest(): T? {
        val item = if (queue.isEmpty()) null else queue.last()
        queue.clear()
        return item
    }

    fun getOldest(): T? {
        return if (queue.isEmpty()) null else queue.removeFirst()
    }

    fun registerListener(listener: QueueListener) {
        listeners.add(listener)
    }

    fun unregisterListener(listener: QueueListener) {
        listeners.remove(listener)
    }
}