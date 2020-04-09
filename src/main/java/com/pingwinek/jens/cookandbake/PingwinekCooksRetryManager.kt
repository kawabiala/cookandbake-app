package com.pingwinek.jens.cookandbake

import com.pingwinek.jens.cookandbake.lib.networkRequest.AbstractNetworkResponseRoutes
import com.pingwinek.jens.cookandbake.lib.networkRequest.NetworkRequest
import com.pingwinek.jens.cookandbake.lib.networkRequest.RetryManager

class PingwinekCooksRetryManager(private val authService: AuthService) : RetryManager {

    override fun retry(
        status: AbstractNetworkResponseRoutes.Result,
        code: Int,
        response: String,
        request: NetworkRequest
    ) {
        authService.onSessionInvalid { authCode, _ ->
            if (authCode == 200) {
                request.obtainNetworkResponseRouter().registerResponseRoute(AbstractNetworkResponseRoutes.Result.SUCCESS, 401) { _, _, _, _ ->
                    //Do nothing, especially don't loop
                }
                request.start()
            }
        }
    }
}