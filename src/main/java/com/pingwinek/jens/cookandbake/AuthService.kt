package com.pingwinek.jens.cookandbake

import android.app.Application
import android.content.Context
import android.content.Intent
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import com.pingwinek.jens.cookandbake.activities.LoginActivity
import com.pingwinek.jens.cookandbake.networkRequest.CookieStore
import com.pingwinek.jens.cookandbake.networkRequest.NetworkRequest
import com.pingwinek.jens.cookandbake.networkRequest.NetworkResponseRouter
import org.json.JSONException
import org.json.JSONObject
import java.lang.Exception
import java.net.URI
import java.util.*
import kotlin.collections.HashMap

class AuthService private constructor(private val application: Application){

    private val method = NetworkRequest.Method.POST
    private val contentType = NetworkRequest.ContentType.APPLICATION_URLENCODED
    private val networkRequest = NetworkRequest.getInstance(application)
    private val prefs = application.getSharedPreferences(SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE)

    fun register(email: String, password: String) {
        val params = HashMap<String, String>()
        params["email"] = email
        params["password"] = password

        val networkResponseRouter = NetworkResponseRouter()
        networkResponseRouter.registerSuccessRoute(200) { response ->
            Log.i(this::class.java.name, "register 200")
        }
        val networkRequest = NetworkRequest.getInstance(application)

        networkRequest.runRequest(
            REGISTERPATH, method, contentType, params,
            NetworkResponseRouter()
        )
    }

    fun login(email: String, password: String) {
        val params = HashMap<String, String>()
        params["email"] = email
        params["password"] = password
        params["uuid"] = getUUID()

        val networkResponseRouter = NetworkResponseRouter()
        networkResponseRouter.registerSuccessRoute(200) { response ->
            Log.i(this::class.java.name, "login 200")
            parseRefreshToken(response)?.let { refreshToken ->
                storeToken(email, refreshToken)
            }
            LocalBroadcastManager.getInstance(application).sendBroadcast(Intent(LOGIN_EVENT))
        }

        networkRequest.runRequest(LOGINPATH , method, contentType, params, networkResponseRouter)
    }

    fun onSessionInvalid() {
        loginWithRefreshToken()
    }

    private fun loginWithRefreshToken() {

        getToken()?.let { params ->
            params["uuid"] = getUUID()
            val networkResponseRouter = NetworkResponseRouter()
            networkResponseRouter.registerSuccessRoute(200) { response ->
                Log.i(this::class.java.name, "refresh 200")
                parseRefreshToken(response)?.let { refreshToken ->
                    params["email"]?.let { storeToken(it, refreshToken) }
                }
                LocalBroadcastManager.getInstance(application).sendBroadcast(Intent(LOGIN_EVENT))
            }
            networkResponseRouter.registerSuccessResponseRoute() { _, response ->
                Log.i(this::class.java.name, "refresh $response")
                LocalBroadcastManager.getInstance(application).sendBroadcast(Intent(LOGOUT_EVENT))
            }

            networkRequest.runRequest(REFRESHPATH, method, contentType, params, networkResponseRouter)
        } ?: kotlin.run {
            Log.i(this::class.java.name, "no refresh credentials")
            LocalBroadcastManager.getInstance(application).sendBroadcast(Intent(LOGOUT_EVENT))
        }

    }

    private fun getToken() : HashMap<String, String>? {
        val params = HashMap<String, String>()

        prefs.getString("email", null)?.let {
            params["email"] = it
        } ?: return null

        prefs.getString("token", null)?.let {
            params["refresh_token"] = it
        } ?: return null

        return params
    }

    private fun storeToken(email: String, token: String) {
        prefs.edit().apply() {
            putString("email", email)
            putString("token", token)
        }.apply()
    }

    private fun parseRefreshToken(response: String) : String? {

        return try {
            JSONObject(response).getString("refresh_token")
        } catch (e: JSONException) {
            null
        }

    }

    fun logout() {
        prefs.edit().apply() {
            remove("email")
            remove("token")
        }.apply()

        CookieStore.removeCookies(URI(BASEURL).host)

        LocalBroadcastManager.getInstance(application).sendBroadcast(Intent(LOGOUT_EVENT))
    }

    private fun getUUID() : String {
        var uuid = prefs.getString("UUID", null)
        if (uuid == null) {
            uuid = UUID.randomUUID().toString()
            prefs.edit().apply() {
                putString("UUID", uuid)
            }.apply()
        }
        return uuid
    }

    companion object : SingletonHolder<AuthService, Application>(::AuthService)
}