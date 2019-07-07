package com.pingwinek.jens.cookandbake.networkRequest

import android.util.Log

const val SUCCESS = "success"
const val FAILED = "failed"

class NetworkResponseRouter {

    private val tag = "NetworkResponseRouter"

    private var defaultRoute: (status: String, code: Int, response: String) -> Unit = {
        status, code, response -> log(status, code, response)
    }

    private var defaultSuccessRoute: (code: Int, response: String) -> Unit = {
            code, response -> log(SUCCESS, code, response)
    }

    private var defaultFailedRoute: (code: Int, response: String) -> Unit = {
            code, response -> log(FAILED, code, response)
    }

    private val successRoutes: HashMap<Int, (response: String) -> Unit> = hashMapOf()

    private val failedRoutes: HashMap<Int, (response: String) -> Unit> = hashMapOf()

    fun routeResponse(status: String, code: Int, response: String) {
        when (status) {
            SUCCESS -> {
                if (successRoutes.containsKey(code)) {
                    successRoutes[code]?.let { it(response) }
                } else {
                    defaultSuccessRoute(code, response)
                }
            }
            FAILED -> {
                if (failedRoutes.containsKey(code)) {
                    failedRoutes[code]?. let { it(response) }
                } else {
                    defaultFailedRoute(code, response)
                }
            }
            else -> {
                defaultRoute(status, code, response)
            }
        }
    }

    fun registerDefaultResponseRoute(responseRoute: (status: String, code: Int, response: String) -> Unit) {
        defaultRoute = responseRoute
    }

    fun registerSuccessResponseRoute(responseRoute: (code: Int, response: String) -> Unit) {
        defaultSuccessRoute = responseRoute
    }

    fun registerFailedResponseRoute(responseRoute: (code: Int, response: String) -> Unit) {
        defaultFailedRoute = responseRoute
    }

    fun registerSuccessRoute(code: Int, responseRoute: (response: String) -> Unit) {
        successRoutes[code] = responseRoute
    }

    fun registerFailedRoute(code: Int, responseRoute: (response: String) -> Unit) {
        failedRoutes[code] = responseRoute
    }

    private fun log(status: String, code: Int, response: String) {
        Log.i(tag, "status: $status - code: $code - response: $response")
    }
}