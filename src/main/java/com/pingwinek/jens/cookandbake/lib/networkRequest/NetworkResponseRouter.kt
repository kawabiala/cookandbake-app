package com.pingwinek.jens.cookandbake.lib.networkRequest

import android.os.Looper
import android.util.Log

class NetworkResponseRouter(val looper: Looper, networkRequest: NetworkRequest) {

    private val responseRoutes = GlobalNetworkResponseRoutes.getNetworkResponseRoutes()

    private val networkResponseHandler: NetworkResponseHandler = object : NetworkResponseHandler(looper) {
        override fun handleResponse(status: AbstractNetworkResponseRoutes.Result, code: Int, response: String) {
            responseRoutes.getResponseRoute(status, code)?.let {
                it(status, code, response, networkRequest)
            }
        }
    }

    fun routeResponse(status: AbstractNetworkResponseRoutes.Result, code: Int, response: String) {
        Log.i(this::class.java.name, "routeResponse with status $status, code $code and response $response")
        networkResponseHandler.sendResponse(status, code, response)
    }

    fun registerDefaultResponseRoute(responseRoute: (result: AbstractNetworkResponseRoutes.Result, code: Int, response: String, request: NetworkRequest) -> Unit) {
        responseRoutes.registerDefaultRoute(responseRoute)
    }

    fun registerDefaultSuccessRoute(responseRoute: (result: AbstractNetworkResponseRoutes.Result, code: Int, response: String, request: NetworkRequest) -> Unit) {
        responseRoutes.registerDefaultSuccessRoute(responseRoute)
    }

    fun registerDefaultFailedRoute(responseRoute: (result: AbstractNetworkResponseRoutes.Result, code: Int, response: String, request: NetworkRequest) -> Unit) {
        responseRoutes.registerDefaultFailedRoute(responseRoute)
    }

    fun registerResponseRoute(
        status: AbstractNetworkResponseRoutes.Result,
        code: Int,
        callback: (result: AbstractNetworkResponseRoutes.Result, code: Int, response: String, request: NetworkRequest) -> Unit
    ) {
        responseRoutes.registerResponseRoute(status, code, callback)
    }
}