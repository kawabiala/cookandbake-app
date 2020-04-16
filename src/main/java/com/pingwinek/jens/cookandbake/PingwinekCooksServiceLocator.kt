package com.pingwinek.jens.cookandbake

import com.pingwinek.jens.cookandbake.lib.ServiceLocator
import com.pingwinek.jens.cookandbake.lib.ServiceNotFoundException
import java.util.*

class PingwinekCooksServiceLocator : ServiceLocator {

    private val services = LinkedList<Any>()

    override fun <T: Any> getService(cls: Class<T>): T {
        val service = getServiceOrNull(cls)
        if (service == null) {
            throw ServiceNotFoundException(cls)
        } else {
            return service
        }
    }

    override fun <T : Any> hasService(cls: Class<T>): Boolean {
        return (getServiceOrNull(cls) != null)
    }

    @Suppress("Unchecked_Cast")
    private fun <T: Any> getServiceOrNull(cls: Class<T>): T? {
        val service = services.find { service ->
            service::class.java == cls
        }
        return if (service != null) {
            service as T
        } else {
            null
        }
    }

    fun <T: Any> registerService(service: T) {
        services.add(service)
    }
}