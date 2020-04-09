package com.pingwinek.jens.cookandbake

import com.pingwinek.jens.cookandbake.lib.ServiceLocator
import java.util.*

class PingwinekCooksServiceLocator : ServiceLocator {

    private val services = LinkedList<Any>()

    @Suppress("Unchecked_Cast")
    override fun <T: Any> getService(cls: Class<T>): T? {
        return services.find { service ->
            service::class.java == cls
        } as T
    }

    fun <T: Any> registerService(service: T) {
        services.add(service)
    }
}