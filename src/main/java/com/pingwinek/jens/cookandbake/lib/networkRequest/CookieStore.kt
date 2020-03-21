package com.pingwinek.jens.cookandbake.lib.networkRequest

import java.net.URI
import java.util.*
import kotlin.collections.HashMap

object CookieStore {

    private val cookieMap = HashMap<String, LinkedList<String>>()

    fun getCookies(host: String) : LinkedList<String> {
        return cookieMap[host] ?: LinkedList()
    }

    fun setCookies(uriHeader: String, cookieHeader: List<String>) {

        val host = URI(uriHeader).host
        val cookies = LinkedList<String>()

        cookieHeader.forEach { _cookie ->
            cookies.push(_cookie)
        }

        setCookies(host, cookies)
    }

    private fun setCookies(host: String, cookies: LinkedList<String>) {
        cookieMap[host] = cookies
    }

    fun removeCookies(host: String) {
        cookieMap.remove(host)
    }
}