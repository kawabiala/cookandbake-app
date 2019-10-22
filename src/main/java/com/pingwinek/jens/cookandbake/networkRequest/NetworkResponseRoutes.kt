package com.pingwinek.jens.cookandbake.networkRequest

import android.util.Log

open class NetworkResponseRoutes {

    enum class Result(val result: String) {
        SUCCESS("success"),
        FAILED("failed")
    }

    private val tag = this::class.java.name

    var defaultRoute: (result: Result, code: Int, response: String, request: NetworkRequest) -> Unit = { result, code, response, request ->
        log(result, code, response)
    }

    var defaultSuccessRoute: (result: Result, code: Int, response: String, request: NetworkRequest) -> Unit = { result, code, response, request ->
        log(result, code, response)
    }

    var defaultFailedRoute: (result: Result, code: Int, response: String, request: NetworkRequest) -> Unit = { result, code, response, request ->
        log(result, code, response)
    }

    val successRoutes: MutableMap<Int, (result: Result, code: Int, response: String, request: NetworkRequest) -> Unit> = mutableMapOf()
    val failedRoutes: MutableMap<Int, (result: Result, code: Int, response: String, request: NetworkRequest) -> Unit> = mutableMapOf()

    private fun log(result: Result, code: Int, response: String) {
        Log.i(tag, "status: $result - code: $code - response: $response")
    }

    fun setResponseRoute(result: Result, code: Int, callback: (result: Result, code: Int, response: String, request: NetworkRequest) -> Unit) {
        when (result) {
            Result.SUCCESS -> successRoutes[code] = callback
            Result.FAILED -> failedRoutes[code] = callback
        }
    }

    fun getResponseRoute(result: Result, code: Int): ((result: Result, code: Int, response: String, request: NetworkRequest) -> Unit)? {
        return when (result) {
            Result.SUCCESS -> successRoutes[code]
            Result.FAILED -> failedRoutes[code]
        }
    }
}


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