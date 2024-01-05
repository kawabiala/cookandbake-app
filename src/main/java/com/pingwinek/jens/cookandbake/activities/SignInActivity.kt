package com.pingwinek.jens.cookandbake.activities

import android.app.AlertDialog
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.pingwinek.jens.cookandbake.R
import com.pingwinek.jens.cookandbake.viewModels.AuthenticationViewModel

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

        // Assign fields to vars

        headerLeftTextView = findViewById(R.id.siHeaderLeft)
        headerRightTextView = findViewById(R.id.siHeaderRight)
        emailEditText = findViewById(R.id.siEmail)
        passwordEditText = findViewById(R.id.siPassword)
        resetTextView = findViewById(R.id.siLostPassword)
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

        authenticationViewModel.result.observe(this) {
            when (authenticationViewModel.result.value) {
                AuthenticationViewModel.ResultType.ACCOUNT_CREATED -> {
                    toast("Account created")
                }
                AuthenticationViewModel.ResultType.VERIFICATION_EMAIL_SENT -> {
                    alert(
                        "Authentication Message",
                        "A verification email has been sent to your inbox.",
                        closeAction)
                }
                AuthenticationViewModel.ResultType.SIGNED_IN -> {
                    alert(
                        "Sign in successful",
                        "you are signed in",
                        closeAction)
                }
                AuthenticationViewModel.ResultType.SIGNED_OUT -> {
                    alert(
                        "Sign out successful",
                        "You can now sign in again") {
                        asRegistrationView = false
                        resetView()
                    }
                }
                AuthenticationViewModel.ResultType.ACCOUNT_DELETED -> {
                    alert(
                        "Account deleted",
                        "You can register a new account") {
                        asRegistrationView = true
                        resetView()
                    }
                }
                AuthenticationViewModel.ResultType.PASSWORD_RESET_SENT -> {
                    alert(
                        "Reset Email Sent",
                        "You have received an email with a reset link. Check your inbox. If you have not received an email, please, make sure that the provided email is correct and that you have an account with PingwinekCooks!")
                }
                AuthenticationViewModel.ResultType.PASSWORD_RESET_CONFIRMED -> {
                    alert(
                        "Password reset",
                        "Your password is reset. You can now sign in.")
                    asRegistrationView = false
                    applyViewSettings(signInView) // resetView won't work in this case
                }
                AuthenticationViewModel.ResultType.EXCEPTION -> {
                    alert(
                        "Exception",
                        authenticationViewModel.errorMessage)
                }
                else -> { alert(
                    "Message",
                    "Sorry, this message should not appear at all.") }
            }
        }

        authenticationViewModel.linkMode.observe(this) {
            if (it == AuthenticationViewModel.EmailLinkMode.RESET) {
                applyViewSettings(resetPasswordView)
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
                AuthenticationViewModel.AuthStatus.SIGNED_OUT -> {
                    if (currentView != resetPasswordView) resetView()
                }
                AuthenticationViewModel.AuthStatus.SIGNED_IN -> {
                    if (currentView != resetPasswordView) applyViewSettings(unverifiedView)
                }
                AuthenticationViewModel.AuthStatus.VERIFIED -> {
                    if (currentView != resetPasswordView) applyViewSettings(verifiedView)
                }
                else -> {}
            }
        }
    }

    override fun onResume() {
        super.onResume()
        resetView()

        authenticationViewModel.checkAuthStatus()
        authenticationViewModel.retrieveEmail()
        authenticationViewModel.checkActionCodeForIntent(intent)
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
            setOnClickListener{ action() }
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
        Log.i(this::class.java.name, "Close")
        startActivity(Intent(this, RecipeListingActivity::class.java))
        finish()
    }

    private val deleteAction: () -> Unit = {
        Log.i(this::class.java.name, "Delete")
        authenticationViewModel.deleteAccount()
    }

    private val registerAction: () -> Unit = {
        Log.i(this::class.java.name, "Register")
        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()
        val dataPolicyChecked = checkBox.isChecked

        authenticationViewModel.register(email, password, dataPolicyChecked)
    }

    private val resetPasswordAction: () -> Unit = {
        Log.i(this::class.java.name, "Reset Password")
        val password = passwordEditText.text.toString()
        authenticationViewModel.resetPassword(password)
    }

    private val sendEmailVerificationAction: () -> Unit = {
        Log.i(this::class.java.name, "Send Email Verification")
        authenticationViewModel.sendVerificationEmail()
    }

    private val sendResetEmailAction: () -> Unit = {
        Log.i(this::class.java.name, "Reset")
        val email = emailEditText.text.toString()
        authenticationViewModel.sendPasswordResetEmail(email)
    }

    private val signInAction: () -> Unit = {
        Log.i(this::class.java.name, "Sign in")
        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()

        authenticationViewModel.signIn(email, password)
    }

    private val signOutAction: () -> Unit = {
        Log.i(this::class.java.name, "Sign out")
        authenticationViewModel.signOut()
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Other functions
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private fun alert(title: String?, message: String, action: (() -> Unit)?) {
        AlertDialog.Builder(this).apply {
            setMessage(message)
            title?.let { setTitle(it) }
            setPositiveButton("Ok") { _, _ ->
                action?.let { it() }
            }.create().show()
        }
    }

    private fun alert(title: String?, message: String) {
        alert(title, message) {}
    }

    private fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}