package com.pingwinek.jens.cookandbake.networkRequest

import android.util.Log

class Cookie(val input: String) {

    var cookieName = ""
    var cookieValue = ""
    var expires = ""
    var maxAge = ""
    var path = ""
    var httpOnly = ""

    init {
        parse(input)
    }

    private fun parse(input: String) {
        input.split("; ").forEachIndexed { index, part ->
            Log.i("cookie", part)

            var key = part
            var value = part

            val par = part.split("=")

            if (par.size == 2) {
                key = par[0]
                value = par[1]
            }

            if (index == 0) {
                cookieName = key
                cookieValue = value
            } else {
                when (key) {
                    params.EXPIRES.value -> expires = value
                    params.MAXAGE.value -> maxAge = value
                    params.PATH.value -> path = value
                    params.HTTPONLY.value -> httpOnly = value
                }
            }
        }
    }

    private enum class params(val value: String) {
        EXPIRES("expires"),
        MAXAGE("Max-Age"),
        PATH("path"),
        HTTPONLY("HttpOnly")
    }
}