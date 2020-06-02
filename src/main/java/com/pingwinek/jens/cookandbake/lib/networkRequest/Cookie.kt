package com.pingwinek.jens.cookandbake.lib.networkRequest

import android.util.Log
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class Cookie(private var domain: String, val cookieString: String) {

    private enum class SameSite(sameSite: String) {
        Strict("strict"),
        Lax("lax"),
        None("none")
    }

    private val datePattern = "EEE, d-MMM-yyyy HH:mm:ss z"

    // The locale, that the server uses when creating the cookie
    private val locale: Locale = Locale.US

    private var name: String? = null
    private var value: String? = null
    private var expires: Date? = null
    private var path: String? = null
    private var secure = false
    private var httpOnly = false
    private var sameSite: SameSite = SameSite.Lax

    init {
        val params = cookieString.split(";")
        params.forEachIndexed { index, param ->
            val pair = param.trim().split("=")
            if (index == 0 && pair.size == 2) {
                name = pair[0]
                value = pair[1]
            } else if (pair.size == 2) {
                when ((pair[0].toLowerCase(Locale.getDefault()))) {
                    "maxage" -> setMaxAge(pair[1])
                    "max-age" -> setMaxAge(pair[1])
                    "expires" -> setExpires(pair[1])
                    "domain" -> this.domain = pair[1]
                    "path" -> path = pair[1]
                    "samesite" -> setSameSite(pair[1])
                }
            } else if (pair.size == 1) {
                when ((pair[0].toLowerCase(Locale.getDefault()))) {
                    "secure" -> secure = true
                    "httponly" -> httpOnly = true
                }
            }
        }
        Log.d(
            this::class.java.name,
            "Cookie parsed: $name, $value, $expires, $path, $domain, $secure, $httpOnly, $sameSite"
        )
    }

    fun getName() = name
    fun getValue() = value
    fun getExpires() = expires
    fun getPath() = path
    fun getDomain() = domain
    fun getSecure() = secure
    fun getHttpOnly() = httpOnly
    //fun getSameSite() = sameSite

    private fun setMaxAge(maxAge: String) {
        val seconds = maxAge.toInt()
        expires = GregorianCalendar(locale).apply {
            add(Calendar.SECOND, seconds)
        }.time
    }

    private fun setExpires(expires: String) {
        // maxAge overwrites expires, but not vice versa
        if (this.expires == null) {
            try {
                this.expires = SimpleDateFormat(datePattern, locale).parse(expires)
            } catch (parseException: ParseException) {
                Log.w(
                    this::class.java.name,
                    "Exception when parsing cookie expiration date $parseException"
                )
                Log.d(
                    this::class.java.name,
                    "Correct date format ${SimpleDateFormat(datePattern, locale).format(Date())}"
                )
            }
        }
    }

    private fun setSameSite(sameSite: String) {
        this.sameSite = when (sameSite.toLowerCase(locale)) {
            "strict" -> SameSite.Strict
            "lax" -> SameSite.Lax
            "none" -> SameSite.None
            else -> SameSite.Lax
        }
    }
}