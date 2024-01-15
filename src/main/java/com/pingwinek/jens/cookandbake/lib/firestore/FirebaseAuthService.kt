package com.pingwinek.jens.cookandbake.lib.firestore

import android.net.Uri
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.FirebaseAuth
import com.pingwinek.jens.cookandbake.BuildConfig
import com.pingwinek.jens.cookandbake.PingwinekCooksApplication
import com.pingwinek.jens.cookandbake.R
import com.pingwinek.jens.cookandbake.utils.SingletonHolder
import java.util.LinkedList
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume

class FirebaseAuthService private constructor(private val application: PingwinekCooksApplication) {

    interface AuthenticationListener {
        fun onLogin()
        fun onLogout()
    }

    private val authListeners = LinkedList<AuthenticationListener>()

    fun registerAuthenticationListener(authenticationListener: AuthenticationListener) {
        authListeners.add(authenticationListener)
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

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private class AuthStateListener(private val continuation: Continuation<Boolean>): FirebaseAuth.AuthStateListener {
        override fun onAuthStateChanged(auth: FirebaseAuth) {
            continuation.resume (auth.currentUser != null)
        }
    }

    data class AuthServiceResult<T>(
        val isSuccess : Boolean = false,
        val result: T?,
        val exception: Exception?) {
    }

    private val auth = FirebaseAuth.getInstance()

    @Throws(SuspendedCoroutineWrapper.SuspendedCoroutineException::class)
    suspend fun getEmailForResetActionCode(actionCode: String): String? {
        val actionCodeResult = SuspendedCoroutineWrapper.suspendedFunction(auth.checkActionCode(actionCode))

        return if (actionCodeResult.operation == 0) {
            actionCodeResult.info?.email
        } else {
            null
        }
    }


    /**
     * Provides the [ActionCodeSettings] for verification and reset password emails. Uses
     * [BuildConfig.APPLICATION_ID] from the gradle build configuration as package name. The
     * redirect URL for redirecting to the app after verification is configured in urls.xml.
     *
     * @param handleInApp false for verification email and true for reset password email
     *
     * The verification email cannot be handled inside the app due to missing verification method
     * in the Firebase Auth api. The reset password email needs to be handled in the app, where
     * we can set a new password.
     */
    private fun getActionCodeSettings(handleInApp: Boolean): ActionCodeSettings {
        return ActionCodeSettings.newBuilder().apply {
            setAndroidPackageName(
                BuildConfig.APPLICATION_ID,
                true,
                null)
            handleCodeInApp = handleInApp
            url = application.getString(R.string.URL_VERIFY_REDIRECT)
        }.build()
    }

    companion object : SingletonHolder<FirebaseAuthService, PingwinekCooksApplication>(::FirebaseAuthService) {

        private const val QP_LINK = "link"
        private const val QP_OOBCODE = "oobCode"

        fun extractActionCode(link: Uri): String? {
            return link.getQueryParameter(QP_LINK)
                ?.let { innerUri ->
                    Uri.parse(innerUri).getQueryParameter(QP_OOBCODE)
                }
        }

    }

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



//    data class AuthenticationResponse(val action: AuthenticationAction, val code: Int, val msg: String?)
}