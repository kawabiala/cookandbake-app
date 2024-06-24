package com.pingwinek.jens.cookandbake.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.lifecycle.ViewModelProvider
import com.pingwinek.jens.cookandbake.PingwinekCooksApplication
import com.pingwinek.jens.cookandbake.R
import com.pingwinek.jens.cookandbake.uiComponents.PingwinekCooksComposableHelpers
import com.pingwinek.jens.cookandbake.uiComponents.pingwinekCooks.PingwinekCooksAppTheme
import com.pingwinek.jens.cookandbake.uiComponents.pingwinekCooks.PingwinekCooksScaffold
import com.pingwinek.jens.cookandbake.uiComponents.signInActivity.ScaffoldContent
import com.pingwinek.jens.cookandbake.viewModels.AuthenticationViewModel
import com.pingwinek.jens.cookandbake.viewModels.UserInfoViewModel

class SignInActivity : AppCompatActivity() {

    /*
    private enum class ButtonRightAction {
        NOTHING,
        REGISTER,
        RESETPASSWORD,
        SENDVERIFICATION,
        SIGNIN,
    }
*/
    /**
     *
     */ /*
    private data class ViewSettings(
        val caption: String? = null,
        val showTabRow: Boolean = true,
        val selectLeftTab: Boolean = true,
//        val buttonLeftCaption: String = "",
        val buttonRightCaption: String = "",
//        val buttonLeftAction: () -> Unit = {},
        val buttonRightAction: ButtonRightAction = ButtonRightAction.NOTHING,
        val editEmail: Boolean = true,
        val showPassword: Boolean = true,
        val showCrashlytics: Boolean = true,
        val showAccountSettings: Boolean = false,
        val showReset: Boolean = false,
        val showPrivacy: Boolean = true,
        val showRightButton: Boolean = true,
        val showLogout: Boolean = false,
        val showDelete: Boolean = false
    )

    private lateinit var registerView: ViewSettings
    private lateinit var signInView: ViewSettings
    private lateinit var resetPasswordView: ViewSettings
    private lateinit var unverifiedView: ViewSettings
    private lateinit var verifiedView: ViewSettings
*/
    private lateinit var authenticationViewModel: AuthenticationViewModel
    private lateinit var userInfoViewModel: UserInfoViewModel

//    private var asRegistrationView: Boolean = true

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Lifecycle Functions
    ////////////////////////////////////////////////////////////////////////////////////////////////

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Define view settings
/*
        registerView = ViewSettings(
//            buttonLeftCaption = getString(R.string.close),
            buttonRightCaption = getString(R.string.register),
//            buttonLeftAction = closeAction,
            buttonRightAction = ButtonRightAction.REGISTER
        )

        signInView = ViewSettings(
            selectLeftTab = false,
//            buttonLeftCaption = getString(R.string.close),
            buttonRightCaption = getString(R.string.login),
//            buttonLeftAction = closeAction,
            buttonRightAction = ButtonRightAction.SIGNIN,
            showCrashlytics = false,
            showReset = true,
            showPrivacy = false,
            showLogout = true,
            showDelete = true
        )

        resetPasswordView = ViewSettings(
            caption = getString(R.string.setPassword),
            showTabRow = false,
//            buttonLeftCaption = getString(R.string.close),
            buttonRightCaption = getString(R.string.setPassword),
//            buttonLeftAction = closeAction,
            buttonRightAction = ButtonRightAction.RESETPASSWORD,
            editEmail = false,
            showReset = true,
            showCrashlytics = false,
            showPrivacy = false
        )

        unverifiedView = ViewSettings(
            caption = getString(R.string.registrationIncomplete),
            showTabRow = false,
//            buttonLeftCaption =  getString(R.string.close),
            buttonRightCaption = getString(R.string.sendVerificationEmail),
//            buttonLeftAction = closeAction,
            buttonRightAction = ButtonRightAction.SENDVERIFICATION,
            editEmail = false,
            showPassword = false,
            showCrashlytics = false,
            showAccountSettings = true,
            showPrivacy = false,
            showLogout = true,
            showDelete = true
        )

        verifiedView = ViewSettings(
            caption = getString(R.string.profile),
            showTabRow = false,
            editEmail = false,
            showPassword = false,
            showAccountSettings = true,
            showCrashlytics = false,
            showReset = true,
            showPrivacy = false,
            showRightButton = false,
            showLogout = true,
            showDelete = true
        )
*/
        // Observers

        authenticationViewModel = ViewModelProvider
            .AndroidViewModelFactory
            .getInstance(application)
            .create(AuthenticationViewModel::class.java)

        userInfoViewModel = ViewModelProvider
            .AndroidViewModelFactory
            .getInstance(application)
            .create(UserInfoViewModel::class.java)

        authenticationViewModel.authStatus.observe(this) {
            authenticationViewModel.retrieveEmail()
            userInfoViewModel.loadData()
        }

        // Google Firebase Testing only
        val googleEmail = "google@pingwinek.de"
        val googlePW = "X@*updr2HtbRaJac"

        authenticationViewModel.onNewEmailChange(googleEmail)
        authenticationViewModel.onPasswordChange(googlePW)
        //authenticationViewModel.signIn()

        val onClose: () -> Unit = {
            startActivity(Intent(this, RecipeListingActivity::class.java))
            finish()
        }

        val optionItems = mutableListOf(
            PingwinekCooksComposableHelpers.OptionItem(
                R.string.dataprotection,
                Icons.Filled.Lock
            ) {
                startActivity(
                    Intent(this@SignInActivity, ImpressumActivity::class.java)
                        .putExtra("title", getString(R.string.dataprotection))
                        .putExtra(
                            "url",
                            (application as PingwinekCooksApplication).getURL(R.string.URL_DATAPROTECTION)
                        )
                )
            },
            PingwinekCooksComposableHelpers.OptionItem(
                R.string.impressum,
                Icons.Filled.Info
            ) {
                startActivity(
                    Intent(this@SignInActivity, ImpressumActivity::class.java)
                        .putExtra("title", getString(R.string.impressum))
                        .putExtra(
                            "url",
                            (application as PingwinekCooksApplication).getURL(R.string.URL_IMPRESSUM)
                        )
                )
            }
        )

        val optionItemRecipe = PingwinekCooksComposableHelpers.OptionItem(
            labelResourceId = PingwinekCooksComposableHelpers.Navigation.RECIPE.label,
            icon = PingwinekCooksComposableHelpers.Navigation.RECIPE.icon
        ) {
            onClose()
        }

        val optionItemProfileLoggedOut = PingwinekCooksComposableHelpers.OptionItem(
            labelResourceId = PingwinekCooksComposableHelpers.Navigation.LOGIN.label,
            icon = PingwinekCooksComposableHelpers.Navigation.LOGIN.icon
        ) {
        }

        val optionItemProfileLoggedIn = PingwinekCooksComposableHelpers.OptionItem(
            labelResourceId = PingwinekCooksComposableHelpers.Navigation.LOGIN.label,
            icon = Icons.Filled.Person
        ) { startActivity(Intent(this, SignInActivity::class.java))
        }

        setContent {
            PingwinekCooksAppTheme {
                PingwinekCooksScaffold(
                    title = getString(R.string.profile),
                    showDropDown = true,
                    dropDownOptions = optionItems,
                    selectedNavigationBarItem = PingwinekCooksComposableHelpers.Navigation.LOGIN.ordinal,
                    navigationBarEnabled = true,
                    navigationBarItems = listOf(
                        optionItemRecipe,
                        optionItemProfileLoggedIn
                    )
                ) { paddingValues ->
                   ScaffoldContent(
                       paddingValues = paddingValues,
                       authenticationViewModel = authenticationViewModel,
                       userInfoViewModel = userInfoViewModel,
                       onClose = onClose
                   )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

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
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        intent.let {
            this.intent = it
        }
    }
/*
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun ScaffoldContent(
        paddingValues: PaddingValues,
        authenticationViewModel: AuthenticationViewModel
    ) {

        val authStatus by authenticationViewModel.authStatus.observeAsState()
        val linkMode by authenticationViewModel.linkMode.observeAsState()
        val authResult by authenticationViewModel.authActionResult.observeAsState()
        val email by authenticationViewModel.email.observeAsState()
        val newEmail by authenticationViewModel.newEmail.observeAsState()
        val password by authenticationViewModel.password.observeAsState()
        val isPrivacyApproved by authenticationViewModel.isPrivacyApproved.observeAsState()

        val userInfoData by userInfoViewModel.userInfoData.observeAsState()

        var asRegistration: Boolean by remember { mutableStateOf(authResult == AuthService.AuthActionResult.DELETE_SUCCEEDED) }

        val viewSettings by remember(authStatus, linkMode, asRegistration) {
            derivedStateOf {
                determineViewSetting(authStatus, linkMode, asRegistration)
            }
        }

        val message: String? by remember(authResult) {
            mutableStateOf(getMessage(authResult))
        }

        val toggleRegistrationView : () -> Unit = { asRegistration = !asRegistration }

        val onResetPasswordClicked : () -> Unit = {
            sendResetEmailAction(if (viewSettings.editEmail) newEmail ?: "" else email ?: "")
        }

        val onCrashlyticsChange : (checked : Boolean) -> Unit = { checked -> userInfoViewModel.saveUserInfo(checked) }

        val onButtonRightChange : () -> Unit = {
            when (viewSettings.buttonRightAction) {
                ButtonRightAction.SIGNIN -> { signInAction() }
                ButtonRightAction.REGISTER -> { registerAction() }
                ButtonRightAction.RESETPASSWORD -> { resetPasswordAction() }
                ButtonRightAction.SENDVERIFICATION -> { sendEmailVerificationAction() }
                ButtonRightAction.NOTHING -> {}
            }
        }

        val onDialogDismissed : () -> Unit = {
            authenticationViewModel.invalidateResult()
        }

        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .verticalScroll(scrollState)
        ) {

            SpacerMedium()

            if (!message.isNullOrEmpty()) {
                BasicAlertDialog(
                    onDismissRequest = onDialogDismissed,
                    properties = DialogProperties(dismissOnBackPress = true),
                    modifier = Modifier
                        .background(color = MaterialTheme.colorScheme.surface)
                        .padding(all = MaterialTheme.spacing.standardPadding)
                ) {
                    Text(message.toString())
                }
            }

            Row {
                if (!viewSettings.caption.isNullOrEmpty()) {
                    ProfileHeader(
                        text = viewSettings.caption!!
                    )
                }

                if (viewSettings.showTabRow)  {
                    SignInTabRow(viewSettings.selectLeftTab, toggleRegistrationView)
                }
            }

            SpacerMedium()

            EditableText(
                text = if (viewSettings.editEmail) {
                    newEmail ?: ""
                } else {
                    getString(R.string.logged_in_as, email ?: "")
                },
                label = getString(R.string.email),
                editable = viewSettings.editEmail,
                onValueChange = { authenticationViewModel.onNewEmailChange(it) }
            )

            if (viewSettings.showPassword) {
                if (!viewSettings.editEmail) {
                    SpacerSmall()
                }

                PasswordField(
                    password = password ?: "",
                    label = getString(R.string.password),
                    showResetPassword = true,
                    resetPasswordText = getString(R.string.lostPassword),
                    onValueChange = { authenticationViewModel.onPasswordChange(it) },
                    onResetPassword = onResetPasswordClicked
                )
            }

            if (viewSettings.showPrivacy) {
                SpacerMedium()
                LabelledCheckBox(
                    label = getString(R.string.declareAcceptanceOfDataprotection),
                    checked = isPrivacyApproved ?: false,
                    onCheckedChange = { authenticationViewModel.onIsPrivacyApprovedChange(it) }
                )
            }

            if (viewSettings.showCrashlytics) {
                SpacerSmall()
                LabelledCheckBox(
                    label = getString(R.string.acceptCrashlytics),
                    checked = userInfoData?.crashlyticsEnabled ?: false,
                    onCheckedChange = onCrashlyticsChange
                )

            }

            SpacerMedium()

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                val buttonLeftColors = if (viewSettings.showRightButton) {
                    ButtonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        disabledContainerColor = MaterialTheme.colorScheme.onSecondary,
                        disabledContentColor = MaterialTheme.colorScheme.secondaryContainer,
                    )
                } else {
                    ButtonDefaults.buttonColors()
                }

                Button(
                    colors = buttonLeftColors,
                    onClick = closeAction
                ) {
                    Text(getString(R.string.close))
                }

                if (viewSettings.showRightButton) {
                    Button(
                        onClick = onButtonRightChange
                    ) {
                        Text(viewSettings.buttonRightCaption)
                    }
                }
            }

            if (viewSettings.showAccountSettings) {
                SpacerMedium()
                AccountSettingsBox(
                    crashlyticsEnabled = userInfoData?.crashlyticsEnabled ?: false,
                    onCrashlyticsChange = onCrashlyticsChange,
                    onResetPasswordClicked = onResetPasswordClicked
                )
            }
        }
    }

    @Composable
    fun ProfileHeader(
        text: String
    ) {
        Row {
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
                )
        }
    }

    @Composable
    fun SignInTabRow(
        highlightLeft: Boolean,
        toggleItem: () -> Unit
    ) {
        PingwinekCooksTabRow(
            selectedItem = if (highlightLeft) { 0 } else { 1 },
            menuItems = LinkedList<PingwinekCooksComposableHelpers.OptionItem>().apply {
                add(
                    PingwinekCooksComposableHelpers.OptionItem(
                        R.string.register, Icons.Filled.Person, toggleItem
                    ))
                add(
                    PingwinekCooksComposableHelpers.OptionItem(
                        R.string.login, Icons.Filled.Person, toggleItem
                    ))
            }
        )
    }

    @Composable
    fun AccountSettingsBox(
        crashlyticsEnabled: Boolean,
        onCrashlyticsChange: (Boolean) -> Unit,
        onResetPasswordClicked: () -> Unit
    ) {
        Expandable(
            headerText = "Account Settings",
            headerTextStyle = MaterialTheme.typography.headlineMedium,
            headerTextColor = MaterialTheme.colorScheme.onSurface,
            contentTextStyle = MaterialTheme.typography.bodyMedium,
            boxColor = MaterialTheme.colorScheme.surfaceContainerLow,
            padding = Dp(20F)
        ) { contentTextStyle ->
            LabelledSwitch(
                label = getString(R.string.acceptCrashlytics),
                labelTextStyle = contentTextStyle,
                checked = crashlyticsEnabled,
                onCheckedChange = onCrashlyticsChange
            )

            HorizontalDivider(
                modifier = Modifier
                    .padding(top = 5.dp, bottom = 5.dp),
                color = MaterialTheme.colorScheme.surfaceContainerHigh
            )

            Row(
                modifier = Modifier.clickable { onResetPasswordClicked() }
            ) {
                Text(
                    text = getString(R.string.lostPassword),
                    style = contentTextStyle,
                )
            }

            HorizontalDivider(
                modifier = Modifier
                    .padding(top = 5.dp, bottom = 5.dp),
                color = MaterialTheme.colorScheme.surfaceContainerHigh
            )


            Row(
                Modifier.clickable(
                    onClick = signOutAction
                )
            ) {
                Text(
                    text = getString(R.string.logout),
                    style = contentTextStyle,
                )
            }

            HorizontalDivider(
                modifier = Modifier
                    .padding(top = 5.dp, bottom = 5.dp),
                color = MaterialTheme.colorScheme.surfaceContainerHigh
            )

            Row(
                Modifier.clickable(
                    onClick = deleteAction
                )
            ) {
                Text(
                    text = getString(R.string.delete),
                    style = contentTextStyle,
                )
            }
        }
    }

    private fun determineViewSetting(
        authStatus: AuthService.AuthStatus?,
        linkMode: AuthenticationViewModel.EmailLinkMode?,
        asRegistration: Boolean
        ) : ViewSettings {
        return when (authStatus) {
            AuthService.AuthStatus.VERIFIED -> {
                if (linkMode == AuthenticationViewModel.EmailLinkMode.RESET) {
                    resetPasswordView
                } else {
                    verifiedView
                    //signInView
                    //registerView
                }
            }

            AuthService.AuthStatus.SIGNED_IN -> {
                when (linkMode) {
                    AuthenticationViewModel.EmailLinkMode.RESET -> {
                        resetPasswordView
                    }

                    AuthenticationViewModel.EmailLinkMode.VERIFIED -> {
                        verifiedView
                    }

                    else -> {
                        unverifiedView
                    }
                }
            }

            AuthService.AuthStatus.SIGNED_OUT -> {
                if (linkMode == AuthenticationViewModel.EmailLinkMode.RESET) {
                    resetPasswordView
                } else if (asRegistration) {
                    registerView
                } else {
                    signInView
                }
            }

            else -> {
                ViewSettings()
            }
        }
    }

    private fun getMessage(authResult: AuthService.AuthActionResult?) : String? {
        return when (authResult) {
            AuthService.AuthActionResult.DELETE_SUCCEEDED -> getString(R.string.accountDeleted)
            AuthService.AuthActionResult.REGISTRATION_SUCCEEDED -> getString(R.string.accountCreated)
            AuthService.AuthActionResult.RESET_PASSWORD_SUCCEEDED -> getString(R.string.PasswordChanged)
            AuthService.AuthActionResult.RESET_PASSWORD_SEND_SUCCEEDED -> getString(R.string.lostPasswordSentLong)
            AuthService.AuthActionResult.SIGNIN_SUCCEEDED -> getString(R.string.loggedIn)
//            AuthService.AuthActionResult.SIGNOUT_SUCCEEDED -> getString(R.string.loggedOut)
            AuthService.AuthActionResult.VERIFICATION_SUCCEEDED -> getString(R.string.confirmationSucceeded)
            AuthService.AuthActionResult.VERIFICATION_SEND_SUCCEEDED -> getString(R.string.confirmationSent)
            AuthService.AuthActionResult.EXC_DATAPOLICY_NOT_ACCEPTED -> getString(R.string.dataProtectionNotChecked)
            AuthService.AuthActionResult.EXC_DELETE_FAILED_WITHOUT_REASON -> getString(R.string.deleteFailed)
            AuthService.AuthActionResult.EXC_DELETE_FAILED_RECENT_LOGIN_REQUIRED -> getString(R.string.login_again)
            AuthService.AuthActionResult.EXC_EMAIL_EMPTY_OR_MALFORMATTED -> getString(R.string.emailMalformatted)
            AuthService.AuthActionResult.EXC_NO_SIGNEDIN_USER -> getString(R.string.noSignedInUser)
            AuthService.AuthActionResult.EXC_PASSWORD_EMPTY -> getString(
                    R.string.passwordMalformatted,
                    AuthService.PasswordPolicy.getPasswordPolicy(getString(R.string.passwordPolicy))
                )
            AuthService.AuthActionResult.EXC_PASSWORD_POLICY_CHECK_NOT_PASSED -> getString(
                    R.string.passwordMalformatted,
                    AuthService.PasswordPolicy.getPasswordPolicy(getString(R.string.passwordPolicy))
                )
            AuthService.AuthActionResult.EXC_REGISTRATION_FAILED_WITHOUT_REASON -> getString(R.string.registrationFailed)
            AuthService.AuthActionResult.EXC_RESET_OR_VERIFY_CODE_INVALID -> getString(R.string.resetOrVerifyCodeMalformed)
            AuthService.AuthActionResult.EXC_RESET_PASSWORD_FAILED_WITHOUT_REASON -> getString(R.string.resetFailed)
            AuthService.AuthActionResult.EXC_RESET_PASSWORD_SEND_FAILED_WITHOUT_REASON -> getString(R.string.sendResetFailed)
            AuthService.AuthActionResult.EXC_SIGNIN_FAILED_WITHOUT_REASON -> getString(R.string.loginFailed)
            AuthService.AuthActionResult.EXC_SIGNOUT_FAILED_WITHOUT_REASON -> getString(R.string.logoutFailed)
            AuthService.AuthActionResult.EXC_USER_ALREADY_EXISTS -> getString(R.string.userAlreadyExists)
            AuthService.AuthActionResult.EXC_VERIFICATION_FAILED_WITHOUT_REASON -> getString(R.string.verificationFailed)
            AuthService.AuthActionResult.EXC_VERIFICATION_SEND_FAILED_WITHOUT_REASON -> getString(R.string.sendVerificationFailed)
            else -> null
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Auth-Actions
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private val closeAction: () -> Unit = {
        startActivity(Intent(this, RecipeListingActivity::class.java))
        finish()
    }

    private val deleteAction: () -> Unit = {
        authenticationViewModel.deleteAccount()
    }

    private val registerAction: () -> Unit = {
        authenticationViewModel.register()
    }

    private val resetPasswordAction: () -> Unit = {
        authenticationViewModel.resetPassword()
    }

    private val sendEmailVerificationAction: () -> Unit = {
        authenticationViewModel.sendVerificationEmail()
    }

    private val sendResetEmailAction: (email: String) -> Unit = { email ->
        authenticationViewModel.sendPasswordResetEmail(email)
    }

    private val signInAction: () -> Unit = {
        authenticationViewModel.signIn()
    }

    private val signOutAction: () -> Unit = {
        authenticationViewModel.signOut()
    }

 */

}