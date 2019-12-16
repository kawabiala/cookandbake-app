package com.pingwinek.jens.cookandbake

import android.app.Application
import android.util.Log
import com.pingwinek.jens.cookandbake.networkRequest.AbstractNetworkResponseRoutes
import com.pingwinek.jens.cookandbake.networkRequest.GlobalNetworkResponseRoutes
import com.pingwinek.jens.cookandbake.networkRequest.NetworkResponseRoutes

class PingwinekCooksApplication : Application() {

    private val tag = this::class.java.name

    override fun onCreate() {
        super.onCreate()

        // Global ResponseRoutings
        /*
        Code 401 signals invalid session, e.g. due to missing or invalid cookie;
        intended behavior is to try authentication based on refresh token
         */
        GlobalNetworkResponseRoutes.registerGlobalResponseRoute(
            AbstractNetworkResponseRoutes.Result.SUCCESS, 401
        ) { result, code, response, request ->
            Log.i(tag, "defaultRoute for 401")
        }
    }
}