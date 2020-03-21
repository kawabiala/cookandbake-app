package com.pingwinek.jens.cookandbake.lib.networkRequest

open class NetworkResponseRoutes : AbstractNetworkResponseRoutes() {

    fun registerDefaultRoute(callback: (result: Result, code: Int, response: String, request: NetworkRequest) -> Unit) {
        defaultRoute = callback
    }

    fun registerDefaultSuccessRoute(callback: (result: Result, code: Int, response: String, request: NetworkRequest) -> Unit) {
        defaultSuccessRoute = callback
    }

    fun registerDefaultFailedRoute(callback: (result: Result, code: Int, response: String, request: NetworkRequest) -> Unit) {
        defaultFailedRoute = callback
    }

    open fun registerResponseRoute(result: Result, code: Int, callback: (result: Result, code: Int, response: String, request: NetworkRequest) -> Unit) {
        when (result) {
            Result.SUCCESS -> successRoutes[code] = callback
            Result.FAILED -> failedRoutes[code] = callback
        }
    }

    open fun getResponseRoute(result: Result, code: Int): ((result: Result, code: Int, response: String, request: NetworkRequest) -> Unit)? {
        return when (result) {
            Result.SUCCESS -> successRoutes[code] ?: defaultSuccessRoute ?: defaultRoute
            Result.FAILED -> failedRoutes[code] ?: defaultFailedRoute ?: defaultRoute
        }
    }
}