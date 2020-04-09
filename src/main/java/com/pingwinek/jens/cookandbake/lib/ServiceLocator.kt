package com.pingwinek.jens.cookandbake.lib

interface ServiceLocator {

    fun <T: Any> getService(cls: Class<T>) : T?
}