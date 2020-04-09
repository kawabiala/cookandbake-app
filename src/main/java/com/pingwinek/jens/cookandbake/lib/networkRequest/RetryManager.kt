package com.pingwinek.jens.cookandbake.lib.networkRequest

interface RetryManager {
    fun retry(status: AbstractNetworkResponseRoutes.Result, code: Int, response: String, request: NetworkRequest)
}