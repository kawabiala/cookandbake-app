package com.pingwinek.jens.cookandbake.sources

import java.util.*

interface Source<T> {

    enum class Status() {
        SUCCESS, FAILURE
    }

    fun getAll(callback: (Status, LinkedList<T>) -> Unit)
    fun get(id: Int, callback: (Status, T?) -> Unit)
    fun new(item: T, callback: (Status, T?) -> Unit)
    fun update(item: T, callback: (Status, T?) -> Unit)
    fun delete(id: Int, callback: (Status) -> Unit)
}