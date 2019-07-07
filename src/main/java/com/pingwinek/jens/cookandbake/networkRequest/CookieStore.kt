package com.pingwinek.jens.cookandbake.networkRequest

import android.util.Log
import java.net.URI
import java.util.*
import kotlin.collections.HashMap

object CookieStore {

    private val tag = this::class.java.name
    private val cookieMap = HashMap<String, LinkedList<String>>()

    fun getCookies(host: String) : LinkedList<String> {
        Log.i(tag, "host: $host")
        Log.i(tag, "getCookies: " + cookieMap[host]?.toString())
        return cookieMap[host] ?: LinkedList<String>()
    }

    fun setCookies(uriHeader: String, cookieHeader: List<String>) {

        val host = URI(uriHeader).host
        val cookies = LinkedList<String>()

        cookieHeader.forEach { _cookie ->
            Log.i(tag, _cookie)
            cookies.push(_cookie)
        }

        setCookies(host, cookies)
    }

    fun setCookies(host: String, cookies: LinkedList<String>) {
        Log.i(tag, "host: $host")
        Log.i(tag, "setCookies: " + cookies.toString())
        cookieMap[host] = cookies
    }
}