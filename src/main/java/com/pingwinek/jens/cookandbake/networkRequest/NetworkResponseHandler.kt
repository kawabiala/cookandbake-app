package com.pingwinek.jens.cookandbake.networkRequest

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message

open class NetworkResponseHandler(val looper: Looper) {

    private val handler = object : Handler(looper) {
        override fun handleMessage(msg: Message?) {
            val status = msg?.data?.getString("status", "") ?: ""
            val result = if (status == AbstractNetworkResponseRoutes.Result.SUCCESS.result) {
                AbstractNetworkResponseRoutes.Result.SUCCESS
            } else {
                AbstractNetworkResponseRoutes.Result.FAILED
            }
            handleResponse(
                result,
                msg?.data?.getInt("code", -1) ?: -1,
                msg?.data?.getString("response", "") ?: ""
            )
        }
    }

    fun sendResponse(status: AbstractNetworkResponseRoutes.Result, code: Int, response: String) {
        handler.sendMessage(handler.obtainMessage().also { msg ->
            msg.data = Bundle().also { data ->
                data.putString("status", status.result)
                data.putInt("code", code)
                data.putString("response", response)
            }
        })
    }

    open fun handleResponse(status: AbstractNetworkResponseRoutes.Result, code: Int, response: String) {
    }
}