package com.pingwinek.jens.cookandbake.lib

interface ServiceLocator {

    @Throws(ServiceNotFoundException::class)
    fun <T: Any> getService(cls: Class<T>) : T

    fun <T: Any> hasService(cls: Class<T>) : Boolean
}