package com.pingwinek.jens.cookandbake

import android.app.Application
import android.content.Context
import android.util.Log
import com.pingwinek.jens.cookandbake.networkRequest.CookieStore
import com.pingwinek.jens.cookandbake.networkRequest.FAILED
import com.pingwinek.jens.cookandbake.networkRequest.NetworkRequestProvider
import com.pingwinek.jens.cookandbake.networkRequest.NetworkResponseRoutes
import org.json.JSONException
import org.json.JSONObject
import java.net.URI
import java.util.*
import kotlin.collections.HashMap

class AuthService private constructor(private val application: Application){

    private val method = NetworkRequestProvider.Method.POST
    private val contentType = NetworkRequestProvider.ContentType.APPLICATION_URLENCODED
    private val networkRequest = NetworkRequestProvider.getInstance(application)
    private val prefs = application.getSharedPreferences(SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE)

    fun hasStoredAccount() : Boolean {
        return (Account.getStoredAccount(application) != null)
    }

    fun isLoggedIn() : Boolean {
        return Account.getStoredAccount(application)?.hasRefreshToken() ?: false
    }

    fun register(
        email: String,
        password: String,
        callback: (code: Int, response: String) -> Unit
    ) {
        val params = HashMap<String, String>()
        params["email"] = email
        params["password"] = password

        val networkResponseRouter = networkRequest.obtainNetworkRequestRouter()
        networkResponseRouter.registerResponseRoute(NetworkResponseRoutes.Result.SUCCESS,200) { status, code, response ->
            Log.i(this::class.java.name, "register 200")
            Account.createStoredAccount(application, email)
            callback(200, response)
        }
        networkResponseRouter.registerDefaultResponseRoute { status, code, response ->
            if (status == NetworkResponseRoutes.Result.FAILED) {
                callback(-1, response)
            } else {
                callback(code, response)
            }
        }

        networkRequest.runRequest(
            REGISTERPATH, method, contentType, params, networkResponseRouter
        )
    }

    fun confirmRegistration(
        tempCode: String,
        callback: (code: Int, response: String) -> Unit
    ) {
        val email = Account.getStoredAccount(application)?.getEmail() ?: return

        val params = HashMap<String, String>()
        params["email"] = email
        params["temp_code"] = tempCode

        val networkResponseRouter = networkRequest.obtainNetworkRequestRouter()
        networkResponseRouter.registerResponseRoute(NetworkResponseRoutes.Result.SUCCESS,200) { status, code, response ->
            Log.i(this::class.java.name, "new Password 200")
            callback(200, response)
        }
        networkResponseRouter.registerDefaultResponseRoute { status, code, response ->
            if (status == NetworkResponseRoutes.Result.FAILED) {
                callback(-1, response)
            } else {
                callback(code, response)
            }
        }

        networkRequest.runRequest(
            CONFIRMREGISTRATIONPATH, method, contentType, params, networkResponseRouter
        )
    }

    fun login(
        email: String,
        password: String,
        callback: (code: Int, response: String) -> Unit)
    {
        val params = HashMap<String, String>()
        params["email"] = email
        params["password"] = password
        params["uuid"] = getUUID()

        val networkResponseRouter = networkRequest.obtainNetworkRequestRouter()
        networkResponseRouter.registerResponseRoute(NetworkResponseRoutes.Result.SUCCESS,200) { status, code, response ->
            Log.i(this::class.java.name, "login 200")
            parseRefreshToken(response)?.let { refreshToken ->
                Account.createStoredAccount(application, email, refreshToken)
            }
            callback(200, response)
        }
        networkResponseRouter.registerResponseRoute(NetworkResponseRoutes.Result.SUCCESS,206) { status, code, response ->
            Log.i(this::class.java.name, "login 206")
            Account.createStoredAccount(application, email)
            callback(206, response)
        }
        networkResponseRouter.registerDefaultResponseRoute { status, code, response ->
            if (status == NetworkResponseRoutes.Result.FAILED) {
                callback(-1, response)
            } else {
                callback(code, response)
            }
        }

        networkRequest.runRequest(LOGINPATH , method, contentType, params, networkResponseRouter)
    }

    fun lostPassword(
        email: String,
        callback: (code: Int, response: String) -> Unit
    ) {
        val params = HashMap<String, String>()
        params["email"] = email

        val networkResponseRouter = networkRequest.obtainNetworkRequestRouter()
        networkResponseRouter.registerResponseRoute(NetworkResponseRoutes.Result.SUCCESS,200) { status, code, response ->
            Log.i(this::class.java.name, "lost Password 200")
            Account.createStoredAccount(application, email)
            callback(200, response)
        }
        networkResponseRouter.registerDefaultResponseRoute { status, code, response ->
            if (status == NetworkResponseRoutes.Result.FAILED) {
                callback(-1, response)
            } else {
                callback(code, response)
            }
        }

        networkRequest.runRequest(
            LOSTPASSWORDPATH, method, contentType, params, networkResponseRouter
        )
    }

    fun newPassword(
        tempCode: String,
        password: String,
        callback: (code: Int, response: String) -> Unit
    ) {
        val email = Account.getStoredAccount(application)?.getEmail() ?: return

        val params = HashMap<String, String>()
        params["email"] = email
        params["temp_code"] = tempCode
        params["password"] = password

        val networkResponseRouter = networkRequest.obtainNetworkRequestRouter()
        networkResponseRouter.registerResponseRoute(NetworkResponseRoutes.Result.SUCCESS,200) { status, code, response ->
            Log.i(this::class.java.name, "new Password 200")
            callback(200, response)
        }
        networkResponseRouter.registerDefaultResponseRoute { status, code, response ->
            if (status == NetworkResponseRoutes.Result.FAILED) {
                callback(-1, response)
            } else {
                callback(code, response)
            }
        }

        networkRequest.runRequest(
            NEWPASSWORDPATH, method, contentType, params, networkResponseRouter
        )
    }

    fun changePassword(
        oldPassword: String,
        newPassword: String,
        callback: (code: Int, response: String) -> Unit
    ) {
        val email = Account.getStoredAccount(application)?.getEmail() ?: return

        val params = HashMap<String, String>()
        params["email"] = email
        params["old_password"] = oldPassword
        params["new_password"] = newPassword

        val networkResponseRouter = networkRequest.obtainNetworkRequestRouter()
        networkResponseRouter.registerResponseRoute(NetworkResponseRoutes.Result.SUCCESS,200) { status, code, response ->
            Log.i(this::class.java.name, "change Password 200")
            callback(200, response)
        }
        networkResponseRouter.registerDefaultResponseRoute { status, code, response ->
            if (status == NetworkResponseRoutes.Result.FAILED) {
                callback(-1, response)
            } else {
                callback(code, response)
            }
        }

        networkRequest.runRequest(
            CHANGEPASSWORDPATH, method, contentType, params, networkResponseRouter
        )
    }

    fun onSessionInvalid(callback: (code: Int, response: String) -> Unit) {
        loginWithRefreshToken(callback)
    }

    private fun loginWithRefreshToken(callback: (code: Int, response: String) -> Unit) {

        if (! isLoggedIn()) {
            callback(-1, "no valid account")
            return
        }

        val account = Account.getStoredAccount(application)
        val params = HashMap<String, String>()

        account?.let {
            params["email"] = it.getEmail() ?: return // should not be null, since we've checked isLoggedIn
            params["refresh_token"] = it.getRefreshToken() ?: return // should not be null, since we've checked isLoggedIn
            params["uuid"] = getUUID()
        }

        val networkResponseRouter = networkRequest.obtainNetworkRequestRouter()
        networkResponseRouter.registerResponseRoute(NetworkResponseRoutes.Result.SUCCESS,200) { status, code, response ->
            Log.i(this::class.java.name, "refresh 200")
            parseRefreshToken(response)?.let { refreshToken ->
                params["email"]?.let {
                    account?.setRefreshToken(refreshToken)
                }
            }
            callback(200, response)
        }
        networkResponseRouter.registerDefaultResponseRoute { status, code, response ->
            if (status == NetworkResponseRoutes.Result.FAILED) {
                callback(-1, response)
            } else {
                callback(code, response)
            }
        }

        networkRequest.runRequest(REFRESHPATH, method, contentType, params, networkResponseRouter)
    }

    private fun parseRefreshToken(response: String) : String? {

        return try {
            JSONObject(response).getString("refresh_token")
        } catch (e: JSONException) {
            null
        }

    }

    fun logout(callback: (code: Int, response: String) -> Unit) {
        val email = Account.getStoredAccount(application)?.getEmail()

        prefs.edit().apply() {
            remove("email")
            remove("token")
        }.apply()

        CookieStore.removeCookies(URI(BASEURL).host)

        if (email == null) return

        val params = HashMap<String, String>()
        params["email"] = email

        val networkResponseRouter = networkRequest.obtainNetworkRequestRouter()
        networkResponseRouter.registerResponseRoute(NetworkResponseRoutes.Result.SUCCESS,200) { status, code, response ->
            Log.i(this::class.java.name, "logout")
            callback(200, response)
        }
        networkResponseRouter.registerDefaultResponseRoute { status, code, response ->
            Log.i(this::class.java.name, "logout on server failed")
            callback(code, response)
        }
/*
        networkRequest.runRequest(
            LOGOUTPATH, method, contentType, params, networkResponseRouter
        )
*/
        callback(-1, "")
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