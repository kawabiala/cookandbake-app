package com.pingwinek.jens.cookandbake

import android.app.Application
import android.content.Context
import android.util.Log
import com.pingwinek.jens.cookandbake.db.DatabaseService
import com.pingwinek.jens.cookandbake.lib.networkRequest.AbstractNetworkResponseRoutes
import com.pingwinek.jens.cookandbake.lib.networkRequest.CookieStore
import com.pingwinek.jens.cookandbake.lib.networkRequest.NetworkRequestProvider
import com.pingwinek.jens.cookandbake.utils.SingletonHolder
import org.json.JSONException
import org.json.JSONObject
import java.net.URI
import java.util.*
import kotlin.collections.HashMap

class AuthService private constructor(private val application: Application){

    private val method = NetworkRequestProvider.Method.POST
    private val contentType = NetworkRequestProvider.ContentType.APPLICATION_URLENCODED
    private val networkRequestProvider = NetworkRequestProvider.getInstance(application)
    private val prefs = application.getSharedPreferences(SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE)

    fun hasStoredAccount() : Boolean {
        return (Account.getStoredAccount(application) != null)
    }

    fun getStoredAccount() : Account? {
        return Account.getStoredAccount(application)
    }

    fun isLoggedIn() : Boolean {
        return Account.getStoredAccount(application)?.hasRefreshToken() ?: false
    }

    fun register(
        email: String,
        password: String,
        dataprotection: Boolean,
        callback: (code: Int, response: String) -> Unit
    ) {
        val params = HashMap<String, String>()
        params["email"] = email
        params["password"] = password
        params["dataprotection"] = dataprotection.toString()

        val networkRequest = networkRequestProvider.getNetworkRequest(REGISTERPATH, method)
        networkRequest.setUploadDataProvider(NetworkRequestProvider.getUploadDataProvider(params, contentType), contentType)
        val networkResponseRouter = networkRequest.obtainNetworkResponseRouter()

        networkResponseRouter.registerResponseRoute(AbstractNetworkResponseRoutes.Result.SUCCESS,201) { status, code, response, request ->
            Log.i(this::class.java.name, "register 201")
            Account.createStoredAccount(application, email)
            callback(201, response)
        }
        networkResponseRouter.registerDefaultResponseRoute { status, code, response, request ->
            if (status == AbstractNetworkResponseRoutes.Result.FAILED) {
                callback(-1, response)
            } else {
                callback(code, response)
            }
        }
        networkRequest.start()
    }

    fun confirmRegistration(
        tempCode: String,
        callback: (code: Int, response: String) -> Unit
    ) {
        val email = Account.getStoredAccount(application)?.getEmail() ?: return

        val params = HashMap<String, String>()
        params["email"] = email
        params["temp_code"] = tempCode

        val networkRequest = networkRequestProvider.getNetworkRequest(CONFIRMREGISTRATIONPATH, method)
        networkRequest.setUploadDataProvider(NetworkRequestProvider.getUploadDataProvider(params, contentType), contentType)
        val networkResponseRouter = networkRequest.obtainNetworkResponseRouter()

        networkResponseRouter.registerResponseRoute(AbstractNetworkResponseRoutes.Result.SUCCESS,200) { status, code, response, request ->
            Log.i(this::class.java.name, "new Password 200")
            callback(200, response)
        }
        networkResponseRouter.registerDefaultResponseRoute { status, code, response, request ->
            if (status == AbstractNetworkResponseRoutes.Result.FAILED) {
                callback(-1, response)
            } else {
                callback(code, response)
            }
        }
        networkRequest.start()
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

        val networkRequest = networkRequestProvider.getNetworkRequest(LOGINPATH, method)
        networkRequest.setUploadDataProvider(NetworkRequestProvider.getUploadDataProvider(params, contentType), contentType)
        val networkResponseRouter = networkRequest.obtainNetworkResponseRouter()

        networkResponseRouter.registerResponseRoute(AbstractNetworkResponseRoutes.Result.SUCCESS,200) { status, code, response, request ->
            Log.i(this::class.java.name, "login 200")
            parseRefreshToken(response)?.let { refreshToken ->
                Account.createStoredAccount(application, email, refreshToken)
            }
            callback(200, response)
        }
        networkResponseRouter.registerResponseRoute(AbstractNetworkResponseRoutes.Result.SUCCESS,206) { status, code, response, request ->
            Log.i(this::class.java.name, "login 206")
            Account.createStoredAccount(application, email)
            callback(206, response)
        }
        networkResponseRouter.registerDefaultResponseRoute { status, code, response, request ->
            if (status == AbstractNetworkResponseRoutes.Result.FAILED) {
                callback(-1, response)
            } else {
                callback(code, response)
            }
        }
        networkRequest.start()
    }

    fun lostPassword(
        email: String,
        callback: (code: Int, response: String) -> Unit
    ) {
        val params = HashMap<String, String>()
        params["email"] = email

        val networkRequest = networkRequestProvider.getNetworkRequest(LOSTPASSWORDPATH, method)
        networkRequest.setUploadDataProvider(NetworkRequestProvider.getUploadDataProvider(params, contentType), contentType)
        val networkResponseRouter = networkRequest.obtainNetworkResponseRouter()

        networkResponseRouter.registerResponseRoute(AbstractNetworkResponseRoutes.Result.SUCCESS,200) { status, code, response, request ->
            Log.i(this::class.java.name, "lost Password 200")
            Account.createStoredAccount(application, email)
            callback(200, response)
        }
        networkResponseRouter.registerDefaultResponseRoute { status, code, response, request ->
            if (status == AbstractNetworkResponseRoutes.Result.FAILED) {
                callback(-1, response)
            } else {
                callback(code, response)
            }
        }
        networkRequest.start()
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

        val networkRequest = networkRequestProvider.getNetworkRequest(NEWPASSWORDPATH, method)
        networkRequest.setUploadDataProvider(NetworkRequestProvider.getUploadDataProvider(params, contentType), contentType)
        val networkResponseRouter = networkRequest.obtainNetworkResponseRouter()

        networkResponseRouter.registerResponseRoute(AbstractNetworkResponseRoutes.Result.SUCCESS,200) { status, code, response, request ->
            Log.i(this::class.java.name, "new Password 200")
            callback(200, response)
        }
        networkResponseRouter.registerDefaultResponseRoute { status, code, response, request ->
            if (status == AbstractNetworkResponseRoutes.Result.FAILED) {
                callback(-1, response)
            } else {
                callback(code, response)
            }
        }
        networkRequest.start()
    }

    fun changePassword(
        oldPassword: String,
        newPassword: String,
        callback: (code: Int, response: String) -> Unit
    ) {
        val email = Account.getStoredAccount(application)?.getEmail()
        if (email == null ) {
            callback(-1, "Kein Konto angemeldet")
            return
        }

        val params = HashMap<String, String>()
        params["email"] = email
        params["password"] = oldPassword
        params["new_password"] = newPassword

        val networkRequest = networkRequestProvider.getNetworkRequest(CHANGEPASSWORDPATH, method)
        networkRequest.setUploadDataProvider(NetworkRequestProvider.getUploadDataProvider(params, contentType), contentType)
        val networkResponseRouter = networkRequest.obtainNetworkResponseRouter()

        networkResponseRouter.registerResponseRoute(AbstractNetworkResponseRoutes.Result.SUCCESS,200) { status, code, response, request ->
            Log.i(this::class.java.name, "change Password 200")
            callback(200, response)
        }
        networkResponseRouter.registerDefaultResponseRoute { status, code, response, request ->
            if (status == AbstractNetworkResponseRoutes.Result.FAILED) {
                callback(-1, response)
            } else {
                callback(code, response)
            }
        }
        networkRequest.start()
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

        val networkRequest = networkRequestProvider.getNetworkRequest(REFRESHPATH, method)
        networkRequest.setUploadDataProvider(NetworkRequestProvider.getUploadDataProvider(params, contentType), contentType)
        val networkResponseRouter = networkRequest.obtainNetworkResponseRouter()

        networkResponseRouter.registerResponseRoute(AbstractNetworkResponseRoutes.Result.SUCCESS,200) { status, code, response, request ->
            Log.i(this::class.java.name, "refresh 200")
            parseRefreshToken(response)?.let { refreshToken ->
                params["email"]?.let {
                    account?.setRefreshToken(refreshToken)
                }
            }
            callback(200, response)
        }
        networkResponseRouter.registerDefaultResponseRoute { status, code, response, request ->
            if (status == AbstractNetworkResponseRoutes.Result.FAILED) {
                callback(-1, response)
            } else {
                callback(code, response)
            }
        }
        networkRequest.start()
    }

    private fun parseRefreshToken(response: String) : String? {

        return try {
            JSONObject(response).getString("refresh_token")
        } catch (e: JSONException) {
            null
        }

    }

    fun logout(callback: (code: Int, response: String) -> Unit) {
        CookieStore.removeCookies(URI(BASEURL).host)
        val email = Account.getStoredAccount(application)?.getEmail() ?: return

        prefs.edit().apply() {
            remove("email")
            remove("token")
        }.apply()

        DatabaseService.resetDatabase(application)

        val params = HashMap<String, String>()
        params["email"] = email
        params["uuid"] = getUUID()

        val networkRequest = networkRequestProvider.getNetworkRequest(LOGOUTPATH, method)
        networkRequest.setUploadDataProvider(NetworkRequestProvider.getUploadDataProvider(params, contentType), contentType)
        val networkResponseRouter = networkRequest.obtainNetworkResponseRouter()

        networkResponseRouter.registerResponseRoute(AbstractNetworkResponseRoutes.Result.SUCCESS,200) { status, code, response, request ->
            Log.i(this::class.java.name, "logout")
            callback(200, response)
        }
        networkResponseRouter.registerDefaultResponseRoute { status, code, response, request ->
            Log.i(this::class.java.name, "logout on server failed")
            callback(code, response)
        }
        networkRequest.start()
    }

    fun unsubscribe(password: String, callback: (code: Int, response: String) -> Unit) {
        val email = Account.getStoredAccount(application)?.getEmail() ?: return

        val params = HashMap<String, String>()
        params["email"] = email
        params["password"] = password

        val networkRequest = networkRequestProvider.getNetworkRequest(UNSUBSCRIBEPATH, method)
        networkRequest.setUploadDataProvider(NetworkRequestProvider.getUploadDataProvider(params, contentType), contentType)
        val networkResponseRouter = networkRequest.obtainNetworkResponseRouter()

        networkResponseRouter.registerResponseRoute(AbstractNetworkResponseRoutes.Result.SUCCESS,200) { status, code, response, request ->
            Log.i(this::class.java.name, "unsubscribe 200")

            prefs.edit().apply() {
                remove("email")
                remove("token")
            }.apply()

            CookieStore.removeCookies(URI(BASEURL).host)

            callback(200, response)
        }
        networkResponseRouter.registerDefaultResponseRoute { status, code, response, request ->
            Log.i(this::class.java.name, "unsubscribe on server failed")
            callback(code, response)
        }

        networkRequest.start()
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