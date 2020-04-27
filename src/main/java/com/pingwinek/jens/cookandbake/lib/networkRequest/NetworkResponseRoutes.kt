package com.pingwinek.jens.cookandbake.lib.networkRequest

open class NetworkResponseRoutes : AbstractNetworkResponseRoutes() {

    fun registerDefaultRoute(callback: (result: Result, code: Int, response: String) -> Unit) {
        defaultRoute = callback
    }

    fun registerDefaultSuccessRoute(callback: (result: Result, code: Int, response: String) -> Unit) {
        defaultSuccessRoute = callback
    }

    fun registerDefaultFailedRoute(callback: (result: Result, code: Int, response: String) -> Unit) {
        defaultFailedRoute = callback
    }

    open fun registerResponseRoute(result: Result, code: Int, callback: (result: Result, code: Int, response: String) -> Unit) {
        when (result) {
            Result.SUCCESS -> successRoutes[code] = callback
            Result.FAILED -> failedRoutes[code] = callback
        }
    }

    open fun getResponseRoute(result: Result, code: Int): ((result: Result, code: Int, response: String) -> Unit)? {
        return when (result) {
            Result.SUCCESS -> successRoutes[code] ?: defaultSuccessRoute ?: defaultRoute
            Result.FAILED -> failedRoutes[code] ?: defaultFailedRoute ?: defaultRoute
        }
    }

    fun clone(): NetworkResponseRoutes {
        return NetworkResponseRoutes().apply {
            defaultRoute?.let { registerDefaultRoute(it) }
            defaultFailedRoute?.let { registerDefaultFailedRoute(it) }
            defaultSuccessRoute?.let { registerDefaultSuccessRoute(it) }
            successRoutes.forEach { route ->
                registerResponseRoute(Result.SUCCESS, route.key, route.value)
            }
            failedRoutes.forEach { route ->
                registerResponseRoute(Result.FAILED, route.key, route.value)
            }
        }
    }
}