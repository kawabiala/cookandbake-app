package com.pingwinek.jens.cookandbake.lib

class ServiceNotFoundException(service: Any) : Exception() {

    private val exception = "Service ${service::class.java} not found."

    override fun toString(): String {
        return "$exception ${super.toString()}"
    }
}