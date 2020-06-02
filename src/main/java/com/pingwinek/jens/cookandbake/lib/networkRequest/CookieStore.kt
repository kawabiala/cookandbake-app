package com.pingwinek.jens.cookandbake.lib.networkRequest

import android.util.Log
import java.net.URI
import java.util.*
import kotlin.collections.HashMap

object CookieStore {

    private val cookieMap = HashMap<String, LinkedList<Cookie>>()

    fun getCookiesAsStrings(host: String) : List<String> {
        cleanCookieMap()

        return cookieMap[host]
            ?.map {
                it.cookieString
        } ?: emptyList()
    }

    fun getCookies(host: String): List<Cookie> {
        cleanCookieMap()

        return cookieMap[host] ?: emptyList()
    }

    fun getCookie(host: String, cookieName: String): Cookie? {
        cleanCookieMap()

        Log.i(this::class.java.name, cookieMap.toString())

        return cookieMap[host]?.find { cookie ->
            cookie.getName() == cookieName
        }
    }

    fun getCookieAsString(host: String, cookieName: String): String? {
        cleanCookieMap()

        val cookie = cookieMap[host]?.find { cookie ->
            cookie.getName() == cookieName
        }

        return cookie?.cookieString
    }

    fun hasCookies(host: String): Boolean {
        cleanCookieMap()

        return cookieMap[host] != null
    }

    fun hasCookie(host: String, cookieName: String): Boolean {
        cleanCookieMap()

        return cookieMap[host]?.find { cookie ->
            cookie.getName() == cookieName
        } != null
    }

    fun setCookies(uriHeader: String, cookieHeader: List<String>) {

        val host = URI(uriHeader).host
        val cookies = LinkedList<Cookie>()

        cookieHeader.forEach { cookie ->
            cookies.push(Cookie(host, cookie))
        }

        cookieMap[host] = cookies
    }

    fun removeCookies(host: String) {
        cookieMap.remove(host)
    }

    private fun cleanCookieMap() {
        cookieMap.forEach { hostEntry ->
            hostEntry.value.removeAll { cookie ->
                cookie.getExpires()?.before(Date()) == true
            }
        }
    }
}