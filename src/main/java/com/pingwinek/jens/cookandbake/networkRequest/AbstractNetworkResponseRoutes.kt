package com.pingwinek.jens.cookandbake.networkRequest

abstract class AbstractNetworkResponseRoutes {

    enum class Result(val result: String) {
        SUCCESS("success"),
        FAILED("failed")
    }

    protected var defaultRoute: ((result: Result, code: Int, response: String, request: NetworkRequest) -> Unit)? = null

    protected var defaultSuccessRoute: ((result: Result, code: Int, response: String, request: NetworkRequest) -> Unit)? = null
    protected var defaultFailedRoute: ((result: Result, code: Int, response: String, request: NetworkRequest) -> Unit)? = null

    protected val successRoutes: MutableMap<Int, (result: Result, code: Int, response: String, request: NetworkRequest) -> Unit> = mutableMapOf()
    protected val failedRoutes: MutableMap<Int, (result: Result, code: Int, response: String, request: NetworkRequest) -> Unit> = mutableMapOf()
}