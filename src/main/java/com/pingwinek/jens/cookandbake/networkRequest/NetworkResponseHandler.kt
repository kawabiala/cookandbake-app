package com.pingwinek.jens.cookandbake.networkRequest

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message

open class NetworkResponseHandler(val looper: Looper) {

    private val handler = object : Handler(looper) {
        override fun handleMessage(msg: Message?) {
            handleResponse(
                msg?.data?.getString("status", "") ?: "",
                msg?.data?.getInt("code", -1) ?: -1,
                msg?.data?.getString("response", "") ?: ""
            )
        }
    }

    fun sendResponse(status: String, code: Int, response: String) {
        handler.sendMessage(handler.obtainMessage().also { msg ->
            msg.data = Bundle().also { data ->
                data.putString("status", status)
                data.putInt("code", code)
                data.putString("response", response)
            }
        })
    }

    open fun handleResponse(status: String, code: Int, response: String) {
    }
}