package com.pingwinek.jens.cookandbake.activities

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
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
    private lateinit var verifyEmailView: ViewSettings
    private lateinit var verifiedView: ViewSettings

    private lateinit var authenticationViewModel: AuthenticationViewModel

    private var verificationLink: String? = null
    private var asRegistrationView: Boolean = true

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

        verifyEmailView = ViewSettings(
            headerLeftCaption = "Confirm with Password",
            buttonLeftCaption = getString(R.string.close),
            buttonRightCaption = "Confirm",
            buttonLeftAction = closeAction,
            buttonRightAction = verifyEmailAction,
            editEmail = false,
            showOnlyLeftHeader = true,
            showReset = true,
            showPrivacy = false
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

        authenticationViewModel.linkMode.observe(this) {
            when(it) {
                AuthenticationViewModel.EmailLinkMode.RESET -> applyViewSettings(resetPasswordView)
                AuthenticationViewModel.EmailLinkMode.VERIFY -> applyViewSettings(verifyEmailView)
                else -> { /* do nothing */ }
            }
        }

        authenticationViewModel.email.observe(this) {
            with(emailEditText.text) {
                clear()
                append(authenticationViewModel.email.value)
            }
        }

        authenticationViewModel.result.observe(this) {
            if (authenticationViewModel.result.value == AuthenticationViewModel.ResultType.EXCEPTION) {
                Log.e(this::class.java.name, "exception: ${authenticationViewModel.errorMessage.value.toString()}")
            } else {
                Log.i(this::class.java.name, authenticationViewModel.result.value?.name ?: "")
            }
            manageView()
        }

        //TODO observer on result + alert etc.

    }

    override fun onResume() {
        super.onResume()
        manageView()
        authenticationViewModel.retrieveEmail()
        authenticationViewModel.checkActionCodeForIntent(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        authenticationViewModel.retrieveEmail()
        intent?.let {
            authenticationViewModel.checkActionCodeForIntent(it)
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Manage View
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private fun manageView() {
        if (!authenticationViewModel.isSignedIn()) {
            if (asRegistrationView) {
                applyViewSettings(registerView)
            } else {
                applyViewSettings(signInView)
            }
        } else if (!authenticationViewModel.isSignedInAndVerified()) {
            applyViewSettings(unverifiedView)
        } else {
            applyViewSettings(verifiedView)
        }
    }

    private fun applyViewSettings(settings: ViewSettings) {
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
                manageView()
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
                manageView()
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

    private val registerAction: () -> Unit = {
        Log.i(this::class.java.name, "Register")
        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()

        authenticationViewModel.register(email, password)
    }

    private val signInAction: () -> Unit = {
        Log.i(this::class.java.name, "Sign in")
        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()

        authenticationViewModel.signIn(email, password)
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

    private val resetPasswordAction: () -> Unit = {
        Log.i(this::class.java.name, "Reset Password")
        val password = passwordEditText.text.toString()
        authenticationViewModel.oobCode.value?.let { oobCode ->
            authenticationViewModel.resetPassword(password, oobCode)
        }
    }

    private val signOutAction: () -> Unit = {
        Log.i(this::class.java.name, "Sign out")
        authenticationViewModel.signOut()
    }

    private val deleteAction: () -> Unit = {
        Log.i(this::class.java.name, "Delete")
        authenticationViewModel.deleteAccount()
    }

    private val verifyEmailAction: () -> Unit = {
        Log.i(this::class.java.name, "Verify Email")
        val password = passwordEditText.text.toString()
        authenticationViewModel.verifyEmail(password, intent)
    }

    private val closeAction: () -> Unit = {
        Log.i(this::class.java.name, "Close")
        finish()
    }
}