package com.pingwinek.jens.cookandbake.networkRequest

object GlobalNetworkResponseRoutes : NetworkResponseRoutes() {

    fun clone(): NetworkResponseRoutes {
        val responseRoutes = NetworkResponseRoutes()
        responseRoutes.defaultRoute = this.defaultRoute
        responseRoutes.defaultSuccessRoute = this.defaultSuccessRoute
        responseRoutes.defaultFailedRoute = this.defaultFailedRoute
        this.successRoutes.forEach() { route ->
            responseRoutes.successRoutes[route.key] = route.value
        }
        this.failedRoutes.forEach() { route ->
            responseRoutes.failedRoutes[route.key] = route.value
        }
        return responseRoutes
    }
}