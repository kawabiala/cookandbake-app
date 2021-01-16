package com.pingwinek.jens.cookandbake

import android.content.Context
import android.util.Log
import com.pingwinek.jens.cookandbake.db.DatabaseService
import com.pingwinek.jens.cookandbake.lib.networkRequest.*
import com.pingwinek.jens.cookandbake.utils.SingletonHolder
import org.json.JSONException
import org.json.JSONObject
import java.lang.ClassCastException
import java.net.CookieHandler
import java.net.CookieManager
import java.net.HttpCookie
import java.net.URI
import java.util.*
import kotlin.collections.HashMap

class AuthService private constructor(private val application: PingwinekCooksApplication) : RefreshManager.Refresh {

    interface AuthenticationListener {
        fun onLogin()
        fun onLogout()
    }

    private val authListeners = LinkedList<AuthenticationListener>()
    private val refreshManager = RefreshManager(this)

    private val method = NetworkRequest.Method.POST
    private val contentType = NetworkRequest.ContentType.APPLICATION_URLENCODED
    private val networkRequestProvider = NetworkRequestProvider.getInstance(application)
    private val prefs = application.getSharedPreferences(SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE)

    fun registerAuthenticationListener(authenticationListener: AuthenticationListener) {
        authListeners.add(authenticationListener)
    }

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

        val networkRequest = networkRequestProvider.getNetworkRequest(application.getURL(R.string.URL_REGISTER), method)
            .apply {
                setOutputString(NetworkRequestProvider.toBody(params))
                setContentType(contentType)
            }

        val networkResponseRouter = networkRequest.obtainNetworkResponseRouter()

        networkResponseRouter.registerResponseRoute(AbstractNetworkResponseRoutes.Result.SUCCESS,201) { _, _, response ->
            Log.i(this::class.java.name, "register 201")
            Account.createStoredAccount(application, email)
            callback(201, response)
        }
        networkResponseRouter.registerDefaultResponseRoute { status, code, response ->
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

        val networkRequest = networkRequestProvider.getNetworkRequest(application.getURL(R.string.URL_CONFIRMREGISTRATION), method)
            .apply {
                setOutputString(NetworkRequestProvider.toBody(params))
                setContentType(contentType)
            }

        val networkResponseRouter = networkRequest.obtainNetworkResponseRouter()

        networkResponseRouter.registerResponseRoute(AbstractNetworkResponseRoutes.Result.SUCCESS,200) { _, _, response ->
            Log.i(this::class.java.name, "new Password 200")
            callback(200, response)
        }
        networkResponseRouter.registerDefaultResponseRoute { status, code, response ->
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

        val networkRequest = networkRequestProvider.getNetworkRequest(application.getURL(R.string.URL_LOGIN), method)
            .apply {
                setOutputString(NetworkRequestProvider.toBody(params))
                setContentType(contentType)
            }

        val networkResponseRouter = networkRequest.obtainNetworkResponseRouter()

        networkResponseRouter.registerResponseRoute(AbstractNetworkResponseRoutes.Result.SUCCESS,200) { _, _, response ->
            Log.i(this::class.java.name, "login 200")
            parseRefreshToken(response)?.let { refreshToken ->
                Account.createStoredAccount(application, email, refreshToken)
            }
            notifyOnLogin()
            callback(200, response)
        }
        networkResponseRouter.registerResponseRoute(AbstractNetworkResponseRoutes.Result.SUCCESS,206) { _, _, response ->
            Log.i(this::class.java.name, "login 206")
            Account.createStoredAccount(application, email)
            callback(206, response)
        }
        networkResponseRouter.registerDefaultResponseRoute { status, code, response ->
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

        val networkRequest = networkRequestProvider.getNetworkRequest(application.getURL(R.string.URL_LOSTPASSWORD), method)
            .apply {
                setOutputString(NetworkRequestProvider.toBody(params))
                setContentType(contentType)
            }

        val networkResponseRouter = networkRequest.obtainNetworkResponseRouter()

        networkResponseRouter.registerResponseRoute(AbstractNetworkResponseRoutes.Result.SUCCESS,200) { _, _, response ->
            Log.i(this::class.java.name, "lost Password 200")
            Account.createStoredAccount(application, email)
            callback(200, response)
        }
        networkResponseRouter.registerDefaultResponseRoute { status, code, response ->
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

        val networkRequest = networkRequestProvider.getNetworkRequest(application.getURL(R.string.URL_NEWPASSWORD), method)
            .apply {
                setOutputString(NetworkRequestProvider.toBody(params))
                setContentType(contentType)
            }

        val networkResponseRouter = networkRequest.obtainNetworkResponseRouter()

        networkResponseRouter.registerResponseRoute(AbstractNetworkResponseRoutes.Result.SUCCESS,200) { _, _, response ->
            Log.i(this::class.java.name, "new Password 200")
            callback(200, response)
        }
        networkResponseRouter.registerDefaultResponseRoute { status, code, response ->
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

        val networkRequest = networkRequestProvider.getNetworkRequest(application.getURL(R.string.URL_CHANGEPASSWORD), method)
            .apply {
                setOutputString(NetworkRequestProvider.toBody(params))
                setContentType(contentType)
            }

        val networkResponseRouter = networkRequest.obtainNetworkResponseRouter()

        networkResponseRouter.registerResponseRoute(AbstractNetworkResponseRoutes.Result.SUCCESS,200) { _, _, response ->
            Log.i(this::class.java.name, "change Password 200")
            callback(200, response)
        }
        networkResponseRouter.registerDefaultResponseRoute { status, code, response ->
            if (status == AbstractNetworkResponseRoutes.Result.FAILED) {
                callback(-1, response)
            } else {
                callback(code, response)
            }
        }
        networkRequest.start()
    }

    fun ensureSession(callback: (Boolean) -> Unit) {
        val httpCookie: HttpCookie? = try {
            val cookieManager = CookieHandler.getDefault() as CookieManager
            cookieManager.cookieStore.get(URI(BuildConfig.DOMAIN)).find { potentialCookie ->
                potentialCookie.name == COOKIE_NAME
            }
        } catch (classCastException: ClassCastException) {
            null
        }
        if (httpCookie == null || httpCookie.hasExpired()) {
            refreshManager.refresh {
                callback(it)
            }
        } else {
            callback(true)
        }
    }

    override fun doRefresh(callback: (code: Int, response: String) -> Unit) {
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

        val networkRequest = networkRequestProvider.getNetworkRequest(application.getURL(R.string.URL_REFRESH), method)
            .apply {
                setOutputString(NetworkRequestProvider.toBody(params))
                setContentType(contentType)
            }

        val networkResponseRouter = networkRequest.obtainNetworkResponseRouter()

        networkResponseRouter.registerResponseRoute(AbstractNetworkResponseRoutes.Result.SUCCESS,200) { _, _, response ->
            Log.i(this::class.java.name, "refresh 200")
            parseRefreshToken(response)?.let { refreshToken ->
                params["email"]?.let {
                    account?.setRefreshToken(refreshToken)
                }
            }
            callback(200, response)
        }
        networkResponseRouter.registerDefaultResponseRoute { status, code, response ->
            if (status == AbstractNetworkResponseRoutes.Result.FAILED) {
                callback(-1, response)
            } else {
                callback(code, response)
            }
        }
        networkRequest.start()
    }

    private fun parseRefreshToken(response: String): String? {

        return try {
            JSONObject(response).getString("refresh_token")
        } catch (e: JSONException) {
            null
        }

    }

    fun logout(callback: (code: Int, response: String) -> Unit) {
        try {
            val cookieManager = CookieHandler.getDefault() as CookieManager
            val httpCookie = cookieManager.cookieStore.get(URI(BuildConfig.DOMAIN)).find { potentialCookie ->
                potentialCookie.name == COOKIE_NAME
            }
            cookieManager.cookieStore.remove(URI(BuildConfig.DOMAIN), httpCookie)
        } catch (classCastException: ClassCastException) {
            Log.w(this::class.java.name, "Could not delete cookies due to exception $classCastException")
        }

        val email = Account.getStoredAccount(application)?.getEmail() ?: return

        prefs.edit().apply {
            remove("email")
            remove("token")
        }.apply()

        DatabaseService.getInstance(application).resetDatabase()
        notifyOnLogout()

        val params = HashMap<String, String>()
        params["email"] = email
        params["uuid"] = getUUID()

        val networkRequest = networkRequestProvider.getNetworkRequest(application.getURL(R.string.URL_LOGOUT), method)
            .apply {
                setOutputString(NetworkRequestProvider.toBody(params))
                setContentType(contentType)
            }

        val networkResponseRouter = networkRequest.obtainNetworkResponseRouter()

        networkResponseRouter.registerResponseRoute(AbstractNetworkResponseRoutes.Result.SUCCESS,200) { _, _, response ->
            Log.i(this::class.java.name, "logout")
            callback(200, response)
        }
        networkResponseRouter.registerDefaultResponseRoute { _, code, response ->
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

        val networkRequest = networkRequestProvider.getNetworkRequest(application.getURL(R.string.URL_UNSUBSCRIBE), method)
            .apply {
                setOutputString(NetworkRequestProvider.toBody(params))
                setContentType(contentType)
            }

        val networkResponseRouter = networkRequest.obtainNetworkResponseRouter()

        networkResponseRouter.registerResponseRoute(AbstractNetworkResponseRoutes.Result.SUCCESS,200) { _, _, response ->
            Log.i(this::class.java.name, "unsubscribe 200")

            prefs.edit().apply {
                remove("email")
                remove("token")
            }.apply()

            try {
                val cookieManager = CookieHandler.getDefault() as CookieManager
                val httpCookie = cookieManager.cookieStore.get(URI(BuildConfig.DOMAIN)).find { potentialCookie ->
                    potentialCookie.name == COOKIE_NAME
                }
                cookieManager.cookieStore.remove(URI(BuildConfig.DOMAIN), httpCookie)
            } catch (classCastException: ClassCastException) {
                Log.w(this::class.java.name, "Could not delete cookies due to exception $classCastException")
            }

            callback(200, response)
        }
        networkResponseRouter.registerDefaultResponseRoute { _, code, response ->
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

    private fun notifyOnLogin() {
        authListeners.forEach { listener ->
            listener.onLogin()
        }
    }

    private fun notifyOnLogout() {
        authListeners.forEach { listener ->
            listener.onLogout()
        }
    }

    companion object : SingletonHolder<AuthService, PingwinekCooksApplication>(::AuthService)
}