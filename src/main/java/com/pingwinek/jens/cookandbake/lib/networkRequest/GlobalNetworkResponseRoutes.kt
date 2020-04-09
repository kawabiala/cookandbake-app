package com.pingwinek.jens.cookandbake.lib.networkRequest

object GlobalNetworkResponseRoutes : AbstractNetworkResponseRoutes() {

    @Suppress("Unused")
    fun registerGlobalDefaultRoute(callback: (result: Result, code: Int, response: String, request: NetworkRequest) -> Unit) {
        defaultRoute = callback
    }

    @Suppress("Unused")
    fun registerGlobalDefaultSuccessRoute(callback: (result: Result, code: Int, response: String, request: NetworkRequest) -> Unit) {
        defaultSuccessRoute = callback
    }

    @Suppress("Unused")
    fun registerGlobalDefaultFailedRoute(callback: (result: Result, code: Int, response: String, request: NetworkRequest) -> Unit) {
        defaultFailedRoute = callback
    }

    fun registerGlobalResponseRoute(result: Result, code: Int, callback: (result: Result, code: Int, response: String, request: NetworkRequest) -> Unit) {
        when (result) {
            Result.SUCCESS -> successRoutes[code] = callback
            Result.FAILED -> failedRoutes[code] = callback
        }
    }

    fun getNetworkResponseRoutes(): NetworkResponseRoutes {
        val responseRoutes = NetworkResponseRoutes()
        this.defaultRoute?.let { responseRoutes.registerDefaultRoute(it) }
        this.defaultSuccessRoute?.let { responseRoutes.registerDefaultSuccessRoute(it) }
        this.defaultFailedRoute?.let { responseRoutes.registerDefaultFailedRoute(it) }
        this.successRoutes.forEach { route ->
            responseRoutes.registerResponseRoute(Result.SUCCESS, route.key, route.value)
        }
        this.failedRoutes.forEach { route ->
            responseRoutes.registerResponseRoute(Result.FAILED, route.key, route.value)
        }

        return responseRoutes
    }
}