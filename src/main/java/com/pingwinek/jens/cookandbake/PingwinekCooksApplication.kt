package com.pingwinek.jens.cookandbake

import android.app.Application
import com.google.firebase.firestore.FirebaseFirestore
import com.pingwinek.jens.cookandbake.lib.ServiceLocator
import com.pingwinek.jens.cookandbake.sources.IngredientSourceFB
import com.pingwinek.jens.cookandbake.sources.RecipeSourceFB
import java.net.URI
import java.util.Locale

class PingwinekCooksApplication : Application() {

    private val tag = this::class.java.name
    private val verificationError = "Verification error"

    private val serviceLocator = PingwinekCooksServiceLocator()

    override fun onCreate() {
        super.onCreate()

        registerServices()
/*
        val cookieManager = CookieManager(HostBasedCookieStore(), CookiePolicy.ACCEPT_ORIGINAL_SERVER)
        CookieHandler.setDefault(cookieManager)
*/
        // Global Response Routes
        // Global response routes are valid unless they are overwritten in a network request

        /*
        Code 401 signals invalid session, e.g. due to missing or invalid cookie;
        we should check for valid session before sending a network request;
         */
/*
        GlobalNetworkResponseRoutes.registerGlobalResponseRoute(
            AbstractNetworkResponseRoutes.Result.SUCCESS, 401
        ) { _, _, _ ->
            Log.w(tag, "Response code 401 might indicate, that we have no valid session. Generally, we should check for a valid session before sending a network request.")
        }
*/
        /*
        Code 404 might signal, that we have a verification error. In case we get
        a verification error, we trigger logout.
         */
/*
        GlobalNetworkResponseRoutes.registerGlobalResponseRoute(
            AbstractNetworkResponseRoutes.Result.SUCCESS, 404
        ) { _, _, response ->
            try {
                val jsonObject = JSONObject(response)
                val error = jsonObject.getString("error")
                if (error == verificationError) {
                    Log.e(tag, "Logging out due to verification error")
                    CoroutineScope(Dispatchers.IO).launch {
                        getServiceLocator().getService(AuthService::class.java).logout()
                    }
                }
            } finally {
                Log.e(tag, response)
            }
        }
*/
    }

    fun getURL(id: Int): String {
        val domain = BuildConfig.DOMAIN
        val locale = resources.configuration.locales[0]
        val language = when (locale.language) {
            Locale.GERMAN.language -> "de"
            Locale.ENGLISH.language -> "en"
            "pl" -> "pl"
            else -> "en"
        }
        return "$domain/${getString(id, language)}"
    }

    fun getHost(): String {
        return URI(BuildConfig.DOMAIN).host
    }


    fun getServiceLocator(): ServiceLocator {
        return serviceLocator
    }

    private fun registerServices() {
        /*
        val internetConnectivityManager = InternetConnectivityManager.getInstance(this)
        serviceLocator.registerService(internetConnectivityManager)

        val authService = AuthService.getInstance(this)
        serviceLocator.registerService(authService)
        */

        val recipeSourceFB = RecipeSourceFB.getInstance(FirebaseFirestore.getInstance())
        serviceLocator.registerService(recipeSourceFB)

        val ingredientSourceFB = IngredientSourceFB.getInstance(FirebaseFirestore.getInstance())
        serviceLocator.registerService(ingredientSourceFB)
    }

}