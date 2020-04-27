package com.pingwinek.jens.cookandbake.lib.networkRequest

interface NetworkResponseRouter {
    fun routeResponse(status: AbstractNetworkResponseRoutes.Result, code: Int, response: String)
    fun registerDefaultResponseRoute(responseRoute: (result: AbstractNetworkResponseRoutes.Result, code: Int, response: String) -> Unit)
    fun registerDefaultSuccessRoute(responseRoute: (result: AbstractNetworkResponseRoutes.Result, code: Int, response: String) -> Unit)
    fun registerDefaultFailedRoute(responseRoute: (result: AbstractNetworkResponseRoutes.Result, code: Int, response: String) -> Unit)
    fun registerResponseRoute(
        status: AbstractNetworkResponseRoutes.Result,
        code: Int,
        callback: (result: AbstractNetworkResponseRoutes.Result, code: Int, response: String) -> Unit
    )
}