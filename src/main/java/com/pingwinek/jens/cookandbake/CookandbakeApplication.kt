package com.pingwinek.jens.cookandbake

import android.app.Application
import android.util.Log
import com.pingwinek.jens.cookandbake.networkRequest.GlobalNetworkResponseRoutes
import com.pingwinek.jens.cookandbake.networkRequest.NetworkRequest
import com.pingwinek.jens.cookandbake.networkRequest.NetworkResponseRoutes

class CookandbakeApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Global ResponseRoutings
        /*
        Code 401 signals invalid session, e.g. due to missing or invalid cookie;
        intended behavior is to try authentication based on refresh token
         */
        GlobalNetworkResponseRoutes.setResponseRoute(
            NetworkResponseRoutes.Result.SUCCESS, 401
        ) { result, code, response, request ->
            refresh(request)
        }
    }

    /*
    Try to authenticate based on refresh token and then re-run the original request
     */
    private fun refresh(networkRequest: NetworkRequest) {
        AuthService.getInstance(this).onSessionInvalid { authCode, authResponse ->
            if (authCode == 200) {
                restartRequest(networkRequest)
            }
        }
    }

    /*
    Reconfigure the original request: overwrite the global 401 routing in order to avoid endless loop
    Then restart it.
     */
    private fun restartRequest(networkRequest: NetworkRequest) {
        networkRequest.obtainNetworkResponseRouter().
            registerResponseRoute(NetworkResponseRoutes.Result.SUCCESS, 401) { result, code, response, request ->
            Log.i(this::class.java.name, "401 loop stopped")
        }
        networkRequest.start()
    }
}