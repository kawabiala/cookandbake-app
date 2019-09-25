package com.pingwinek.jens.cookandbake

import android.app.Application
import android.content.Context
import android.content.Intent
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import com.pingwinek.jens.cookandbake.activities.LoginActivity
import com.pingwinek.jens.cookandbake.networkRequest.*
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

    fun register(email: String, password: String, callback: (code: Int, response: String) -> Unit) {
        val params = HashMap<String, String>()
        params["email"] = email
        params["password"] = password

        val networkResponseRouter = networkRequest.obtainNetworkRequestRouter()
        networkResponseRouter.registerSuccessRoute(200) {
            Log.i(this::class.java.name, "register 200")
            storeEmail(email)
            callback(200, it)
        }
        networkResponseRouter.registerDefaultResponseRoute { status, code, response ->
            if (status == FAILED) {
                callback(-1, response)
            } else {
                callback(code, response)
            }
        }

        networkRequest.runRequest(
            REGISTERPATH, method, contentType, params, networkResponseRouter
        )
    }

    fun lostPassword(email: String) {

        val networkResponseRouter = networkRequest.obtainNetworkRequestRouter()
    }

    fun newPassword(tempCode: String, password: String) {
        val  email = getEmail() ?: return

        val params = HashMap<String, String>()
        params["email"] = email
        params["temp_code"] = tempCode
        params["password"] = password

        val networkResponseRouter = networkRequest.obtainNetworkRequestRouter()
        networkResponseRouter.registerSuccessRoute(200) { response ->
            Log.i(this::class.java.name, "new Password 200")
        }
        Log.i(this::class.java.name, "newPassword with tempCode $tempCode")
/*
        networkRequest.runRequest(
            NEWPASSWORDPATH, method, contentType, params, networkResponseRouter
        )*/
    }

    fun changePassword(oldEmail: String, newEmail: String) {

        val networkResponseRouter = networkRequest.obtainNetworkRequestRouter()
    }

    fun confirmRegistration(tempCode: String) {
        val  email = getEmail() ?: return

        val params = HashMap<String, String>()
        params["email"] = email
        params["temp_code"] = tempCode

        val networkResponseRouter = networkRequest.obtainNetworkRequestRouter()
        networkResponseRouter.registerSuccessRoute(200) { response ->
            Log.i(this::class.java.name, "new Password 200")
        }
        Log.i(this::class.java.name, "confirmRegistration with tempCode $tempCode")

        networkRequest.runRequest(
            CONFIRMREGISTRATIONPATH, method, contentType, params, networkResponseRouter
        )
    }

    fun login(email: String, password: String) {
        val params = HashMap<String, String>()
        params["email"] = email
        params["password"] = password
        params["uuid"] = getUUID()

        val networkResponseRouter = networkRequest.obtainNetworkRequestRouter()
        networkResponseRouter.registerSuccessRoute(200) { response ->
            Log.i(this::class.java.name, "login 200")
            parseRefreshToken(response)?.let { refreshToken ->
                storeToken(email, refreshToken)
            }
            LocalBroadcastManager.getInstance(application).sendBroadcast(Intent(LOGIN_EVENT))
        }
        networkResponseRouter.registerSuccessRoute(206) { response ->
            Log.i(this::class.java.name, "login 206")
            storeEmail(email)
            //TODO Ask user if he wants to be sent a confirmation mail
        }

        networkRequest.runRequest(LOGINPATH , method, contentType, params, networkResponseRouter)
    }

    /*
    Checks locally, if we have a remembered user. This user may have a refresh token, if he has already logged in. But he
    won't have it, if he's newly registered.
     */
    fun isRemembered() : Boolean {
        return (getEmail() != null)
    }

    fun onSessionInvalid() {
        loginWithRefreshToken()
    }

    private fun loginWithRefreshToken() {

        getToken()?.let { params ->
            params["uuid"] = getUUID()
            val networkResponseRouter = networkRequest.obtainNetworkRequestRouter()
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
        } ?: run {
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

    private fun getEmail() : String? {
        return prefs.getString("email", null) ?: null
    }

    private fun storeToken(email: String, token: String) {
        prefs.edit().apply() {
            putString("email", email)
            putString("token", token)
        }.apply()
    }

    private fun storeEmail(email: String) {
        prefs.edit().apply {
            putString("email", email)
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
        logout { _, _ ->}
    }

    fun logout(callback: (code: Int, response: String) -> Unit) {
        val params = HashMap<String, String>()
        params["email"] = getEmail() ?: ""

        prefs.edit().apply() {
            remove("email")
            remove("token")
        }.apply()

        CookieStore.removeCookies(URI(BASEURL).host)

        val networkResponseRouter = networkRequest.obtainNetworkRequestRouter()
        networkResponseRouter.registerSuccessRoute(200) { response ->
            Log.i(this::class.java.name, "logout")
            //callback(200, response)
        }
        networkResponseRouter.registerDefaultResponseRoute { status, code, response ->
            Log.i(this::class.java.name, "logout on server failed")
            //callback(code, response)
        }
/*
        networkRequest.runRequest(
            LOGOUTPATH, method, contentType, params, networkResponseRouter
        )
*/
        callback(-1, "")

        LocalBroadcastManager.getInstance(application).sendBroadcast(Intent(LOGOUT_EVENT))
    }

    private fun getUUID() : String {
        var uuid = prefs.getString("UUID", null)
        if (uuid == null) {
            uuid = UUID.randomUUID().toString()
            prefs.edit().apply {
                putString("UUID", uuid)
            }.apply()
        }
        return uuid
    }

    companion object : SingletonHolder<AuthService, Application>(::AuthService)
}