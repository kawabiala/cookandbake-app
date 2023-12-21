package com.pingwinek.jens.cookandbake

import android.content.Context
import android.util.Log
import com.pingwinek.jens.cookandbake.lib.networkRequest.NetworkRequest
import com.pingwinek.jens.cookandbake.lib.networkRequest.NetworkRequestProvider
import com.pingwinek.jens.cookandbake.lib.networkRequest.NetworkResponse
import com.pingwinek.jens.cookandbake.utils.SingletonHolder
import org.json.JSONException
import org.json.JSONObject
import java.net.CookieHandler
import java.net.CookieManager
import java.net.HttpCookie
import java.net.URI
import java.util.LinkedList
import java.util.UUID

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

    fun getStoredAccount() : Account? {
        return Account.getStoredAccount(application)
    }

    fun hasStoredAccount() : Boolean {
        return (Account.getStoredAccount(application) != null)
    }

    fun isLoggedIn() : Boolean {
        return Account.getStoredAccount(application)?.hasRefreshToken() ?: false
    }

    suspend fun changePassword(
        oldPassword: String,
        newPassword: String
    ) : AuthenticationResponse {
        val email = getStoredAccount()?.getEmail()
            ?: return AuthenticationResponse(AuthenticationAction.CHANGE_PASSWORD, -1, null)

        val params = HashMap<String, String>()
        params["email"] = email
        params["password"] = oldPassword
        params["new_password"] = newPassword

        val networkRequest = networkRequestProvider.getNetworkRequest(application.getURL(R.string.URL_CHANGEPASSWORD), method)
            .apply {
                setOutputString(NetworkRequestProvider.toBody(params))
                setContentType(contentType)
            }

        val networkResponse = networkRequest.start()
        return translateResponse(AuthenticationAction.CHANGE_PASSWORD, networkResponse)
    }

    suspend fun confirmRegistration(
        tempCode: String
    ) : AuthenticationResponse {
        val email = getStoredAccount()?.getEmail()
            ?: return AuthenticationResponse(AuthenticationAction.CONFIRM, -1, null)

        val params = HashMap<String, String>()
        params["email"] = email
        params["temp_code"] = tempCode

        val networkRequest = networkRequestProvider.getNetworkRequest(application.getURL(R.string.URL_CONFIRMREGISTRATION), method)
            .apply {
                setOutputString(NetworkRequestProvider.toBody(params))
                setContentType(contentType)
            }

        val networkResponse = networkRequest.start()
        return translateResponse(AuthenticationAction.CONFIRM, networkResponse)
    }

    override suspend fun doRefresh() : Boolean {
        val account = getStoredAccount() ?: return false
        val email = account.getEmail() ?: return false
        val refreshToken = account.getRefreshToken() ?: return false

        val params = HashMap<String, String>().apply {
            put("email", email)
            put("refresh_token", refreshToken)
            put("uuid", getUUID())
        }

        val networkRequest = networkRequestProvider.getNetworkRequest(application.getURL(R.string.URL_REFRESH), method)
            .apply {
                setOutputString(NetworkRequestProvider.toBody(params))
                setContentType(contentType)
            }

        val networkResponse = networkRequest.start()
        return if (networkResponse.succeeded() && networkResponse.code == 200) {
            val responseString = networkResponse.responseAsString() ?: return false
            val newToken = parseRefreshToken(responseString) ?: return false
            account.setRefreshToken(newToken)
            true
        } else {
            false
        }
    }

    suspend fun ensureSession() : Boolean {
        val httpCookie: HttpCookie? = try {
            val cookieManager = CookieHandler.getDefault() as CookieManager
            cookieManager.cookieStore.get(URI(BuildConfig.DOMAIN)).find { cookie ->
                cookie.name == COOKIE_NAME
            }
        } catch (classCastException: ClassCastException) {
            null
        }
        return if (httpCookie == null || httpCookie.hasExpired()) {
            refreshManager.refresh()
        } else {
            true
        }
    }

    suspend fun login(
        email: String,
        password: String
    ) : AuthenticationResponse {
        val params = HashMap<String, String>()
        params["email"] = email
        params["password"] = password
        params["uuid"] = getUUID()

        val networkRequest = networkRequestProvider.getNetworkRequest(application.getURL(R.string.URL_LOGIN), method)
            .apply {
                setOutputString(NetworkRequestProvider.toBody(params))
                setContentType(contentType)
            }

        val networkResponse = networkRequest.start()
        val response = translateResponse(AuthenticationAction.LOGIN, networkResponse)

        if (response.code == 200) {
            response.msg?.let {
                parseRefreshToken(it)?.let { refreshToken ->
                    Account.createStoredAccount(application, email, refreshToken)
                }
            }
            notifyOnLogin()
        } else if (response.code == 206) {
            Account.createStoredAccount(application, email)
        }

        return response
    }

    suspend fun logout() : AuthenticationResponse {
        // first we remove the session cookie, if there is any
        try {
            val cookieManager = CookieHandler.getDefault() as CookieManager
            val httpCookie = cookieManager.cookieStore.get(URI(BuildConfig.DOMAIN)).find { cookie ->
                cookie.name == COOKIE_NAME
            }
            cookieManager.cookieStore.remove(URI(BuildConfig.DOMAIN), httpCookie)
        } catch (classCastException: ClassCastException) {
            Log.w(this::class.java.name, "Could not delete cookies due to exception $classCastException")
        }

        // if we have no account or no email, we are not logged in
        val email = getStoredAccount()?.getEmail()
            ?: return AuthenticationResponse(AuthenticationAction.LOGOUT, -1, null)

        // logout locally by removing refresh token and email from local storage
        prefs.edit().apply {
            remove("email")
            remove("token")
        }.apply()

        // Delete all data from the local database
        //DatabaseService.getInstance(application).resetDatabase()
        notifyOnLogout()

        // Finally, we try to logout remote
        // Basically, this is not really necessary, just in case someone hacked the session
        // or even managed to get the refresh token
        val params = HashMap<String, String>()
        params["email"] = email
        params["uuid"] = getUUID()

        val networkRequest = networkRequestProvider.getNetworkRequest(application.getURL(R.string.URL_LOGOUT), method)
            .apply {
                setOutputString(NetworkRequestProvider.toBody(params))
                setContentType(contentType)
            }

        val networkResponse = networkRequest.start()
        return translateResponse(AuthenticationAction.LOGOUT, networkResponse)
    }

    suspend fun lostPassword(
        email: String
    ) : AuthenticationResponse {
        val params = HashMap<String, String>()
        params["email"] = email

        val networkRequest = networkRequestProvider.getNetworkRequest(application.getURL(R.string.URL_LOSTPASSWORD), method)
            .apply {
                setOutputString(NetworkRequestProvider.toBody(params))
                setContentType(contentType)
            }

        val networkResponse = networkRequest.start()
        val response = translateResponse(AuthenticationAction.LOST_PASSWORD, networkResponse)

        if (response.code == 200) {
            Account.createStoredAccount(application, email)
        }

        return response
    }

    suspend fun newPassword(
        tempCode: String,
        password: String
    ) : AuthenticationResponse {
        val email = Account.getStoredAccount(application)?.getEmail()
            ?: return AuthenticationResponse(AuthenticationAction.NEW_PASSWORD, -1, null)

        val params = HashMap<String, String>()
        params["email"] = email
        params["temp_code"] = tempCode
        params["password"] = password

        val networkRequest = networkRequestProvider.getNetworkRequest(application.getURL(R.string.URL_NEWPASSWORD), method)
            .apply {
                setOutputString(NetworkRequestProvider.toBody(params))
                setContentType(contentType)
            }

        val networkResponse = networkRequest.start()
        return translateResponse(AuthenticationAction.NEW_PASSWORD, networkResponse)
    }

    suspend fun register(
        email: String,
        password: String,
        dataprotection: Boolean
    ) : AuthenticationResponse {
        val params = HashMap<String, String>()
        params["email"] = email
        params["password"] = password
        params["dataprotection"] = dataprotection.toString()

        val networkRequest = networkRequestProvider.getNetworkRequest(application.getURL(R.string.URL_REGISTER), method)
            .apply {
                setOutputString(NetworkRequestProvider.toBody(params))
                setContentType(contentType)
            }

        val networkResponse = networkRequest.start()
        return translateResponse(AuthenticationAction.REGISTER, networkResponse)
    }

    suspend fun unsubscribe(
        password: String
    ) : AuthenticationResponse {
        val email = getStoredAccount()?.getEmail()
            ?: return AuthenticationResponse(AuthenticationAction.UNSUBSCRIBE, -1, null)

        val params = HashMap<String, String>()
        params["email"] = email
        params["password"] = password

        val networkRequest = networkRequestProvider.getNetworkRequest(application.getURL(R.string.URL_UNSUBSCRIBE), method)
            .apply {
                setOutputString(NetworkRequestProvider.toBody(params))
                setContentType(contentType)
            }

        val networkResponse = networkRequest.start()
        var response = translateResponse(AuthenticationAction.UNSUBSCRIBE, networkResponse)

        if (response.code == 200) {
            val logoutResponse = logout()
            response = AuthenticationResponse(AuthenticationAction.UNSUBSCRIBE, logoutResponse.code, logoutResponse.msg)
            /*
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

             */
        }

        return response
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

    private fun parseRefreshToken(response: String): String? {
        return try {
            JSONObject(response).getString("refresh_token")
        } catch (e: JSONException) {
            null
        }
    }

    private fun translateResponse(
        action: AuthenticationAction,
        response: NetworkResponse
    ) : AuthenticationResponse {
        return if (response.succeeded()) {
            AuthenticationResponse(
                    action,
                    response.code,
                    response.responseAsString()
                )
        } else {
            AuthenticationResponse(action, -1, null)
        }
    }

    companion object : SingletonHolder<AuthService, PingwinekCooksApplication>(::AuthService)

    enum class AuthenticationAction {
        CHANGE_PASSWORD,
        CONFIRM,
        LOGIN,
        LOGOUT,
        LOST_PASSWORD,
        NEW_PASSWORD,
        REGISTER,
        UNSUBSCRIBE
    }

    data class AuthenticationResponse(val action: AuthenticationAction, val code: Int, val msg: String?)
}