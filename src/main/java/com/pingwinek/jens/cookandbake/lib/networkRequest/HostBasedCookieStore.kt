package com.pingwinek.jens.cookandbake.lib.networkRequest

import java.net.CookieStore
import java.net.HttpCookie
import java.net.URI
import java.util.*

class HostBasedCookieStore: CookieStore {

    private val cookieMap = mutableMapOf<String, LinkedList<HttpCookie>>()

    override fun add(uri: URI?, cookie: HttpCookie?) {
        uri?.let { nonNullUri ->
            val host = nonNullUri.host
            cookie?.let { nonNullCookie ->
                cookieMap[host] = cookieMap[host] ?: LinkedList()
                cookieMap[host]?.add(nonNullCookie)
            }
        }
    }

    override fun get(uri: URI?): MutableList<HttpCookie> {
        removeExpired()
        return uri?.let { nonNullUri ->
            val host = nonNullUri.host
            cookieMap[host]
        } ?: mutableListOf()
    }

    override fun getCookies(): MutableList<HttpCookie> {
        removeExpired()
        return LinkedList<HttpCookie>().apply {
            cookieMap.forEach { entry ->
                addAll(entry.value)
            }
        }
    }

    override fun getURIs(): MutableList<URI> {
        removeExpired()
        return LinkedList<URI>().apply {
            cookieMap.keys.forEach { host ->
                add(URI(host))
            }
        }
    }

    override fun remove(uri: URI?, cookie: HttpCookie?): Boolean {
        var result = false

        uri?.let { nonNullUri ->
            val host = nonNullUri.host
            cookie?.let { nonNullCookie ->
                result = cookieMap[host]?.remove(nonNullCookie) ?: false
            }
        }

        return result
    }

    override fun removeAll(): Boolean {
        cookieMap.clear()
        return true
    }

    private fun removeExpired() {
        cookieMap.forEach { entry ->
            entry.value.forEach { cookie ->
                if (cookie.hasExpired()) {
                    remove(URI(entry.key), cookie)
                }
            }
        }
    }
}