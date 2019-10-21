package com.pingwinek.jens.cookandbake.networkRequest

import android.os.Looper
import android.util.Log

const val SUCCESS = "success"
const val FAILED = "failed"

class NetworkResponseRouter(val looper: Looper) {

    private val tag = "NetworkResponseRouter"

    private val responseRoutes = GlobalNetworkResponseRoutes.clone()

    private val networkResponseHandler: NetworkResponseHandler = object : NetworkResponseHandler(looper) {
        override fun handleResponse(status: NetworkResponseRoutes.Result, code: Int, response: String) {
            when (status) {
                NetworkResponseRoutes.Result.SUCCESS -> {
                    if (responseRoutes.successRoutes.containsKey(code)) {
                        responseRoutes.successRoutes[code]?.let { it(status, code, response) }
                    } else {
                        responseRoutes.defaultSuccessRoute(status, code, response)
                    }
                }
                NetworkResponseRoutes.Result.FAILED -> {
                    if (responseRoutes.failedRoutes.containsKey(code)) {
                        responseRoutes.failedRoutes[code]?. let { it(status, code, response) }
                    } else {
                        responseRoutes.defaultFailedRoute(status, code, response)
                    }
                }
                else -> {
                    responseRoutes.defaultRoute(status, code, response)
                }
            }
        }
    }
/*
    private var defaultRoute: (status: String, code: Int, response: String) -> Unit = {
        status, code, response -> log(status, code, response)
    }

    private var defaultSuccessRoute: (code: Int, response: String) -> Unit = { code, response ->
        defaultRoute(SUCCESS, code, response)
    }

    private var defaultFailedRoute: (code: Int, response: String) -> Unit = { code, response ->
        defaultRoute(FAILED, code, response)
    }

    private val successRoutes: HashMap<Int, (response: String) -> Unit> = hashMapOf()

    private val failedRoutes: HashMap<Int, (response: String) -> Unit> = hashMapOf()
*/
    fun routeResponse(status: NetworkResponseRoutes.Result, code: Int, response: String) {
        networkResponseHandler.sendResponse(status, code, response)
    }

    fun registerDefaultResponseRoute(responseRoute: (status: NetworkResponseRoutes.Result, code: Int, response: String) -> Unit) {
        responseRoutes.defaultRoute = responseRoute
    }

    fun registerSuccessResponseRoute(responseRoute: (status: NetworkResponseRoutes.Result, code: Int, response: String) -> Unit) {
        responseRoutes.defaultSuccessRoute = responseRoute
    }

    fun registerFailedResponseRoute(responseRoute: (status: NetworkResponseRoutes.Result, code: Int, response: String) -> Unit) {
        responseRoutes.defaultFailedRoute = responseRoute
    }
/*
    fun registerSuccessRoute(code: Int, responseRoute: (response: String) -> Unit) {
        successRoutes[code] = responseRoute
    }

    fun registerFailedRoute(code: Int, responseRoute: (response: String) -> Unit) {
        failedRoutes[code] = responseRoute
    }
*/
    fun registerResponseRoute(
        status: NetworkResponseRoutes.Result,
        code: Int,
        callback: (status: NetworkResponseRoutes.Result, code: Int, response: String) -> Unit
    ) {
        responseRoutes.setResponseRoute(status, code, callback)
    }

    private fun log(status: String, code: Int, response: String) {
        Log.i(tag, "status: $status - code: $code - response: $response")
    }
}