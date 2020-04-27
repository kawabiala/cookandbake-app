package com.pingwinek.jens.cookandbake.lib.networkRequest

import android.os.Looper
import android.util.Log

class NetworkResponseRouterImpl(val looper: Looper, private val responseRoutes: NetworkResponseRoutes) : NetworkResponseRouter {

    constructor(looper: Looper) : this(looper, GlobalNetworkResponseRoutes.getNetworkResponseRoutes())

    private val networkResponseHandler: NetworkResponseHandler = object : NetworkResponseHandler(looper) {
        override fun handleResponse(status: AbstractNetworkResponseRoutes.Result, code: Int, response: String) {
            responseRoutes.getResponseRoute(status, code)?.let {
                it(status, code, response)
            }
        }
    }

    override fun routeResponse(status: AbstractNetworkResponseRoutes.Result, code: Int, response: String) {
        Log.i(this::class.java.name, "routeResponse with status $status, code $code and response $response")
        networkResponseHandler.sendResponse(status, code, response)
    }

    override fun registerDefaultResponseRoute(responseRoute: (result: AbstractNetworkResponseRoutes.Result, code: Int, response: String) -> Unit) {
        responseRoutes.registerDefaultRoute(responseRoute)
    }

    @Suppress("Unused")
    override fun registerDefaultSuccessRoute(responseRoute: (result: AbstractNetworkResponseRoutes.Result, code: Int, response: String) -> Unit) {
        responseRoutes.registerDefaultSuccessRoute(responseRoute)
    }

    @Suppress("Unused")
    override fun registerDefaultFailedRoute(responseRoute: (result: AbstractNetworkResponseRoutes.Result, code: Int, response: String) -> Unit) {
        responseRoutes.registerDefaultFailedRoute(responseRoute)
    }

    override fun registerResponseRoute(
        status: AbstractNetworkResponseRoutes.Result,
        code: Int,
        callback: (result: AbstractNetworkResponseRoutes.Result, code: Int, response: String) -> Unit
    ) {
        responseRoutes.registerResponseRoute(status, code, callback)
    }

    fun clone(): NetworkResponseRouter {
        return NetworkResponseRouterImpl(looper, responseRoutes.clone())
    }
}