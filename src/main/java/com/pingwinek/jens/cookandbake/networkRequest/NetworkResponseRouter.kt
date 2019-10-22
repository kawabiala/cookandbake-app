package com.pingwinek.jens.cookandbake.networkRequest

import android.os.Looper

class NetworkResponseRouter(val looper: Looper, networkRequest: NetworkRequest) {

    private val responseRoutes = GlobalNetworkResponseRoutes.clone()

    private val networkResponseHandler: NetworkResponseHandler = object : NetworkResponseHandler(looper) {
        override fun handleResponse(status: NetworkResponseRoutes.Result, code: Int, response: String) {
            when (status) {
                NetworkResponseRoutes.Result.SUCCESS -> {
                    if (responseRoutes.successRoutes.containsKey(code)) {
                        responseRoutes.successRoutes[code]?.let { it(status, code, response, networkRequest) }
                    } else {
                        responseRoutes.defaultSuccessRoute(status, code, response, networkRequest)
                    }
                }
                NetworkResponseRoutes.Result.FAILED -> {
                    if (responseRoutes.failedRoutes.containsKey(code)) {
                        responseRoutes.failedRoutes[code]?. let { it(status, code, response, networkRequest) }
                    } else {
                        responseRoutes.defaultFailedRoute(status, code, response, networkRequest)
                    }
                }
            }
        }
    }

    fun routeResponse(status: NetworkResponseRoutes.Result, code: Int, response: String) {
        networkResponseHandler.sendResponse(status, code, response)
    }

    fun registerDefaultResponseRoute(responseRoute: (result: NetworkResponseRoutes.Result, code: Int, response: String, request: NetworkRequest) -> Unit) {
        responseRoutes.defaultRoute = responseRoute
    }

    fun registerDefaultSuccessRoute(responseRoute: (result: NetworkResponseRoutes.Result, code: Int, response: String, request: NetworkRequest) -> Unit) {
        responseRoutes.defaultSuccessRoute = responseRoute
    }

    fun registerDefaultFailedRoute(responseRoute: (result: NetworkResponseRoutes.Result, code: Int, response: String, request: NetworkRequest) -> Unit) {
        responseRoutes.defaultFailedRoute = responseRoute
    }

    fun registerResponseRoute(
        status: NetworkResponseRoutes.Result,
        code: Int,
        callback: (result: NetworkResponseRoutes.Result, code: Int, response: String, request: NetworkRequest) -> Unit
    ) {
        responseRoutes.setResponseRoute(status, code, callback)
    }
}