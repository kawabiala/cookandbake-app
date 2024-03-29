package com.pingwinek.jens.cookandbake.activities

import android.app.AlertDialog
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.pingwinek.jens.cookandbake.R
import com.pingwinek.jens.cookandbake.lib.AuthService
import com.pingwinek.jens.cookandbake.viewModels.AuthenticationViewModel
import com.pingwinek.jens.cookandbake.viewModels.UserInfoViewModel

class SignInActivity : BaseActivity() {

    /**
     *
     */
    private data class ViewSettings(
        val headerLeftCaption: String = "",
        val headerRightCaption: String = "",
        val buttonLeftCaption: String = "",
        val buttonRightCaption: String = "",
        val buttonLeftAction: () -> Unit = {},
        val buttonRightAction: () -> Unit = {},
        val highlightLeftHeader: Boolean = true,
        val showOnlyLeftHeader: Boolean = false,
        val editEmail: Boolean = true,
        val showPassword: Boolean = true,
        val showCrashlytics: Boolean = true,
        val showReset: Boolean = false,
        val showPrivacy: Boolean = true,
        val showLeftButton: Boolean = true,
        val showLogout: Boolean = false,
        val showDelete: Boolean = false
    )

    private lateinit var headerLeftTextView: TextView
    private lateinit var headerRightTextView: TextView
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var resetTextView: TextView
    private lateinit var acceptCrashlyticsView: CheckBox
    private lateinit var acceptCrashlyticsTextView: TextView
    private lateinit var checkBox: CheckBox
    private lateinit var acceptanceTextView: TextView
    private lateinit var buttonLeft: Button
    private lateinit var buttonRight: Button
    private lateinit var logoutTextView: TextView
    private lateinit var deleteTextView: TextView

    private lateinit var registerView: ViewSettings
    private lateinit var signInView: ViewSettings
    private lateinit var resetPasswordView: ViewSettings
    private lateinit var unverifiedView: ViewSettings
    private lateinit var verifiedView: ViewSettings

    private lateinit var authenticationViewModel: AuthenticationViewModel
    private lateinit var userInfoViewModel: UserInfoViewModel

    private var asRegistrationView: Boolean = true
    private var currentView: ViewSettings = ViewSettings()

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Lifecycle Functions
    ////////////////////////////////////////////////////////////////////////////////////////////////

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        addContentView(R.layout.activity_signin)

        authenticationViewModel = ViewModelProvider
            .AndroidViewModelFactory
            .getInstance(application)
            .create(AuthenticationViewModel::class.java)

        userInfoViewModel = ViewModelProvider
            .AndroidViewModelFactory
            .getInstance(application)
            .create(UserInfoViewModel::class.java)

        // Assign fields to vars

        headerLeftTextView = findViewById(R.id.siHeaderLeft)
        headerRightTextView = findViewById(R.id.siHeaderRight)
        emailEditText = findViewById(R.id.siEmail)
        passwordEditText = findViewById(R.id.siPassword)
        resetTextView = findViewById(R.id.siLostPassword)
        acceptCrashlyticsView = findViewById(R.id.siCheckCrashlyticsBox)
        acceptCrashlyticsTextView = findViewById(R.id.siAcceptCrashlytics)
        checkBox = findViewById(R.id.siCheckBox)
        acceptanceTextView = findViewById(R.id.siAcceptance)
        buttonLeft = findViewById(R.id.siCancelButton)
        buttonRight = findViewById(R.id.siLoginButton)
        logoutTextView = findViewById(R.id.siLogout)
        deleteTextView = findViewById(R.id.siDelete)

        // Define view settings

        registerView = ViewSettings(
            headerLeftCaption = getString(R.string.register),
            headerRightCaption = getString(R.string.login),
            buttonLeftCaption = getString(R.string.close),
            buttonRightCaption = getString(R.string.register),
            buttonLeftAction = closeAction,
            buttonRightAction = registerAction
        )

        signInView = ViewSettings(
            headerLeftCaption = getString(R.string.register),
            headerRightCaption = getString(R.string.login),
            buttonLeftCaption = getString(R.string.close),
            buttonRightCaption = getString(R.string.login),
            buttonLeftAction = closeAction,
            buttonRightAction = signInAction,
            highlightLeftHeader = false,
            showReset = true,
            showPrivacy = false,
            showLogout = true,
            showDelete = true
        )

        resetPasswordView = ViewSettings(
            headerLeftCaption = getString(R.string.setPassword),
            buttonLeftCaption = getString(R.string.close),
            buttonRightCaption = getString(R.string.setPassword),
            buttonLeftAction = closeAction,
            buttonRightAction = resetPasswordAction,
            editEmail = false,
            showOnlyLeftHeader = true,
            showReset = true,
            showCrashlytics = false,
            showPrivacy = false
        )

        unverifiedView = ViewSettings(
            headerLeftCaption = getString(R.string.registrationIncomplete),
            buttonLeftCaption =  getString(R.string.close),
            buttonRightCaption = getString(R.string.sendVerificationEmail),
            buttonLeftAction = closeAction,
            buttonRightAction = sendEmailVerificationAction,
            showOnlyLeftHeader = true,
            editEmail = false,
            showPassword = false,
            showCrashlytics = false,
            showPrivacy = false,
            showLogout = true,
            showDelete = true
        )

        verifiedView = ViewSettings(
            headerLeftCaption = getString(R.string.profile),
            buttonRightCaption = getString(R.string.close),
            buttonRightAction = closeAction,
            showOnlyLeftHeader = true,
            editEmail = false,
            showPassword = false,
            showReset = true,
            showPrivacy = false,
            showLeftButton = false,
            showLogout = true,
            showDelete = true
        )

        // Observers

        authenticationViewModel.authActionResult.observe(this) {
            when (authenticationViewModel.authActionResult.value) {
                AuthService.AuthActionResult.DELETE_SUCCEEDED -> {
                    authMessage(
                        getString(R.string.accountDeleted)) {
                        asRegistrationView = true
                        resetView()
                    }
                }
                AuthService.AuthActionResult.REGISTRATION_SUCCEEDED -> {
                    toast(getString(R.string.accountCreated))
                }
                AuthService.AuthActionResult.RESET_PASSWORD_SUCCEEDED -> {
                    authMessage(
                        getString(R.string.PasswordChanged)) {
                        asRegistrationView = false
                        applyViewSettings(signInView) // resetView won't work in this case
                    }
                }
                AuthService.AuthActionResult.RESET_PASSWORD_SEND_SUCCEEDED -> {
                    authMessage(
                        getString(R.string.lostPasswordSentLong))
                }
                AuthService.AuthActionResult.SIGNIN_SUCCEEDED -> {
                    authMessage(
                        getString(R.string.loggedIn),
                        closeAction)
                }
                AuthService.AuthActionResult.SIGNOUT_SUCCEEDED -> {
                    authMessage(
                        getString(R.string.loggedOut)) {
                        asRegistrationView = false
                        resetView()
                    }
                }
                AuthService.AuthActionResult.VERIFICATION_SUCCEEDED -> {
                    authMessage(
                        getString(R.string.confirmationSucceeded)) {
                        closeAction
                    }
                }
                AuthService.AuthActionResult.VERIFICATION_SEND_SUCCEEDED -> {
                    authMessage(
                        getString(R.string.confirmationSent),
                        closeAction)
                }
                AuthService.AuthActionResult.EXC_DATAPOLICY_NOT_ACCEPTED -> {
                    errorMessage(
                        getString(R.string.dataProtectionNotChecked)
                    )
                }
                AuthService.AuthActionResult.EXC_DELETE_FAILED_WITHOUT_REASON -> {
                    errorMessage(
                        getString(R.string.deleteFailed)
                    )
                }
                AuthService.AuthActionResult.EXC_EMAIL_EMPTY_OR_MALFORMATTED -> {
                    errorMessage(
                        getString(R.string.emailMalformatted)
                    )
                }
                AuthService.AuthActionResult.EXC_NO_SIGNEDIN_USER -> {
                    errorMessage(
                        getString(R.string.noSignedInUser)
                    )
                }
                AuthService.AuthActionResult.EXC_PASSWORD_EMPTY -> {
                    errorMessage(
                        getString(
                            R.string.passwordMalformatted,
                            AuthService.PasswordPolicy.getPasswordPolicy(getString(R.string.passwordPolicy))
                        )
                    )
                }
                AuthService.AuthActionResult.EXC_PASSWORD_POLICY_CHECK_NOT_PASSED -> {
                    errorMessage(
                        getString(
                            R.string.passwordMalformatted,
                            AuthService.PasswordPolicy.getPasswordPolicy(getString(R.string.passwordPolicy))
                        )
                    )
                }
                AuthService.AuthActionResult.EXC_REGISTRATION_FAILED_WITHOUT_REASON -> {
                    errorMessage(
                        getString(R.string.registrationFailed)
                    )
                }
                AuthService.AuthActionResult.EXC_RESET_PASSWORD_FAILED_WITHOUT_REASON -> {
                    errorMessage(
                        getString(R.string.resetFailed)
                    )
                }
                AuthService.AuthActionResult.EXC_RESET_PASSWORD_SEND_FAILED_WITHOUT_REASON -> {
                    errorMessage(
                        getString(R.string.sendResetFailed)
                    )
                }
                AuthService.AuthActionResult.EXC_SIGNIN_FAILED_WITHOUT_REASON -> {
                    errorMessage(
                        getString(R.string.loginFailed)
                    )
                }
                AuthService.AuthActionResult.EXC_SIGNOUT_FAILED_WITHOUT_REASON -> {
                    errorMessage(
                        getString(R.string.logoutFailed)
                    )
                }
                AuthService.AuthActionResult.EXC_USER_ALREADY_EXISTS -> TODO()
                AuthService.AuthActionResult.EXC_VERIFICATION_FAILED_WITHOUT_REASON -> {
                    errorMessage(
                        getString(R.string.verificationFailed)) {
                        applyViewSettings(unverifiedView)
                    }
                }
                AuthService.AuthActionResult.EXC_VERIFICATION_SEND_FAILED_WITHOUT_REASON -> {
                    errorMessage(
                        getString(R.string.sendVerificationFailed)
                    )
                }
                null -> TODO()
            }
        }
/*
        authenticationViewModel.result.observe(this) {
            when (authenticationViewModel.result.value) {
                AuthenticationViewModel.ResultType.ACCOUNT_CREATED -> {
                    toast(getString(R.string.accountCreated))
                }
                AuthenticationViewModel.ResultType.VERIFICATION_EMAIL_SENT -> {
                    alert(
                        getString(R.string.authenticationMessage),
                        getString(R.string.confirmationSent),
                        closeAction)
                }
                AuthenticationViewModel.ResultType.SIGNED_IN -> {
                    alert(
                        getString(R.string.authenticationMessage),
                        getString(R.string.loggedIn),
                        closeAction)
                }
                AuthenticationViewModel.ResultType.SIGNED_OUT -> {
                    alert(
                        getString(R.string.authenticationMessage),
                        getString(R.string.loggedOut)) {
                        asRegistrationView = false
                        resetView()
                    }
                }
                AuthenticationViewModel.ResultType.ACCOUNT_DELETED -> {
                    alert(
                        getString(R.string.authenticationMessage),
                        getString(R.string.accountDeleted)) {
                        asRegistrationView = true
                        resetView()
                    }
                }
                AuthenticationViewModel.ResultType.PASSWORD_RESET_SENT -> {
                    alert(
                        getString(R.string.authenticationMessage),
                        getString(R.string.lostPasswordSentLong))
                }
                AuthenticationViewModel.ResultType.PASSWORD_RESET_CONFIRMED -> {
                    alert(
                        getString(R.string.authenticationMessage),
                        getString(R.string.PasswordChanged))
                    asRegistrationView = false
                    applyViewSettings(signInView) // resetView won't work in this case
                }
                AuthenticationViewModel.ResultType.EXCEPTION -> {
                    alert(
                        getString(R.string.errorMessage),
                        authenticationViewModel.errorMessage)
                }
                else -> {}
            }
        }

 */

        authenticationViewModel.linkMode.observe(this) {
            when (it) {
                AuthenticationViewModel.EmailLinkMode.RESET -> applyViewSettings(resetPasswordView)
                AuthenticationViewModel.EmailLinkMode.VERIFIED -> applyViewSettings(verifiedView)
                else -> {}
            }
        }

        authenticationViewModel.email.observe(this) {
            with(emailEditText.text) {
                clear()
                append(authenticationViewModel.email.value)
            }
        }

        authenticationViewModel.authStatus.observe(this) { authStatus ->
            when (authStatus) {
                AuthService.AuthStatus.SIGNED_OUT -> {
                    if (currentView != resetPasswordView) resetView()
                }
                AuthService.AuthStatus.SIGNED_IN -> {
                    if (currentView != resetPasswordView) applyViewSettings(unverifiedView)
                }
                AuthService.AuthStatus.VERIFIED -> {
                    if (currentView != resetPasswordView) applyViewSettings(verifiedView)
                }
                else -> {}
            }
        }

        userInfoViewModel.userInfoData.observe(this) { userInfo ->
            acceptCrashlyticsView.isChecked = userInfo.crashlyticsEnabled
        }
    }

    override fun onResume() {
        super.onResume()
        resetView()

        authenticationViewModel.checkAuthStatus()
        authenticationViewModel.retrieveEmail()
        authenticationViewModel.checkActionCodeForIntent(intent)

        userInfoViewModel.loadData()
    }

    /**
     * Called before onResume:
     * if there is a new intent, update the intent for this activity - otherwise the original
     * intent would be used; compare documentation in [android.app.Activity]
     */
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let {
            this.intent = it
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Manage View
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private fun resetView() {
        if (asRegistrationView) {
            applyViewSettings(registerView)
        } else {
            applyViewSettings(signInView)
        }
    }

    private fun applyViewSettings(settings: ViewSettings) {
        currentView = settings

        adaptHeaderLeft(settings.headerLeftCaption, settings.showOnlyLeftHeader, settings.highlightLeftHeader)
        adaptHeaderRight(settings.headerRightCaption, settings.showOnlyLeftHeader, settings.highlightLeftHeader)
        adaptEmail(settings.editEmail)
        adaptPassword(settings.showPassword)
        adaptReset(settings.showReset)
        adaptCrashlytics(settings.showCrashlytics)
        adaptPrivacy(settings.showPrivacy)
        adaptButtonLeft(settings.buttonLeftCaption, settings.showLeftButton, settings.buttonLeftAction)
        adaptButtonRight(settings.buttonRightCaption, settings.buttonRightAction)
        adaptLogout(settings.showLogout)
        adaptDelete(settings.showDelete)
    }

    private fun adaptHeaderLeft(caption: String, showOnlyLeftHeader: Boolean, highlightLeftHeader: Boolean) {
        headerLeftTextView.apply {
            text = caption
            background = if (showOnlyLeftHeader) {
                null
            } else if (highlightLeftHeader) {
                ColorDrawable(resources.getColor(R.color.colorPrimary, null))
            } else {
                ColorDrawable(resources.getColor(R.color.colorDisabled, null))
            }
            setOnClickListener {
                asRegistrationView = true
                resetView()
            }
        }
    }

    private fun adaptHeaderRight(caption: String, showOnlyLeftHeader: Boolean, highlightLeftHeader: Boolean) {
        headerRightTextView.apply {
            text = caption
            if (showOnlyLeftHeader) {
                isVisible = false
            } else if (highlightLeftHeader) {
                isVisible = true
                background = ColorDrawable(resources.getColor(R.color.colorDisabled, null))
            } else {
                isVisible = true
                background = ColorDrawable(resources.getColor(R.color.colorPrimary, null))
            }
            setOnClickListener {
                asRegistrationView = false
                resetView()
            }
        }
    }

    private fun adaptEmail(editEmail: Boolean) {
        emailEditText.isEnabled = editEmail
    }

    private fun adaptPassword(showPassword: Boolean) {
        passwordEditText.apply {
            isVisible = showPassword
        }
    }

    private fun adaptCrashlytics(showCrashlytics: Boolean) {
        acceptCrashlyticsView.isVisible = showCrashlytics
        acceptCrashlyticsTextView.isVisible = showCrashlytics
        acceptCrashlyticsView.setOnClickListener {
            crashlyticsAction()
        }
    }

    private fun adaptReset(showReset: Boolean) {
        resetTextView.apply {
            isVisible = showReset
            setOnClickListener { sendResetEmailAction() }
        }
    }

    private fun adaptPrivacy(showPrivacy: Boolean) {
        checkBox.isVisible = showPrivacy
        acceptanceTextView.isVisible = showPrivacy
    }

    private fun adaptButtonLeft(caption: String, showLeftButton: Boolean, action: () -> Unit) {
        buttonLeft.apply {
            text = caption
            isVisible = showLeftButton
            setOnClickListener { action() }
        }
    }

    private fun adaptButtonRight(caption: String, action: () -> Unit) {
        buttonRight.apply {
            text = caption
            setOnClickListener { action() }
        }
    }

    private fun adaptLogout(showLogout: Boolean) {
        logoutTextView.apply {
            isVisible = showLogout
            setOnClickListener { signOutAction() }
        }
    }

    private fun adaptDelete(showDelete: Boolean) {
        deleteTextView.apply {
            isVisible = showDelete
            setOnClickListener { deleteAction() }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Auth-Actions
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private val closeAction: () -> Unit = {
        startActivity(Intent(this, RecipeListingActivity::class.java))
        finish()
    }

    private val crashlyticsAction: () -> Unit = {
        userInfoViewModel.saveUserInfo(acceptCrashlyticsView.isChecked)
    }

    private val deleteAction: () -> Unit = {
        authenticationViewModel.deleteAccount()
    }

    private val registerAction: () -> Unit = {
        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()
        val dataPolicyChecked = checkBox.isChecked

        authenticationViewModel.register(email, password, dataPolicyChecked)
    }

    private val resetPasswordAction: () -> Unit = {
        val password = passwordEditText.text.toString()
        authenticationViewModel.resetPassword(password)
    }

    private val sendEmailVerificationAction: () -> Unit = {
        authenticationViewModel.sendVerificationEmail()
    }

    private val sendResetEmailAction: () -> Unit = {
        val email = emailEditText.text.toString()
        authenticationViewModel.sendPasswordResetEmail(email)
    }

    private val signInAction: () -> Unit = {
        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()

        authenticationViewModel.signIn(email, password)
    }

    private val signOutAction: () -> Unit = {
        authenticationViewModel.signOut()
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Other functions
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private fun alert(title: String?, message: String, action: (() -> Unit)?) {
        AlertDialog.Builder(this).apply {
            setMessage(message)
            title?.let { setTitle(it) }
            setPositiveButton(getString(R.string.ok)) { _, _ ->
                action?.let { it() }
            }.create().show()
        }
    }

    private fun authMessage(message: String) {
        authMessage(message) {}
    }

    private fun authMessage(message: String, action: () -> Unit) {
        alert(getString(R.string.authenticationMessage), message, action)
    }

    private fun errorMessage(message: String) {
        errorMessage(message) {}
    }

    private fun errorMessage(message: String, action: () -> Unit) {
        alert(getString(R.string.errorMessage), message, action)
    }

    private fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}