package com.pingwinek.jens.cookandbake

import android.app.Application
import com.google.firebase.firestore.FirebaseFirestore
import com.pingwinek.jens.cookandbake.lib.ServiceLocator
import com.pingwinek.jens.cookandbake.lib.UriUtils
import com.pingwinek.jens.cookandbake.sources.IngredientSourceFB
import com.pingwinek.jens.cookandbake.sources.RecipeSourceFB
import com.pingwinek.jens.cookandbake.sources.UserInfoSourceFB
import java.util.Locale

class PingwinekCooksApplication : Application() {

    private val serviceLocator = PingwinekCooksServiceLocator()

    override fun onCreate() {
        super.onCreate()
        registerServices()
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

    /*
    fun getHost(): String {
        return URI(BuildConfig.DOMAIN).host
    }

     */


    fun getServiceLocator(): ServiceLocator {
        return serviceLocator
    }

    private fun registerServices() {
        val recipeSourceFB = RecipeSourceFB.getInstance(FirebaseFirestore.getInstance())
        serviceLocator.registerService(recipeSourceFB)

        val ingredientSourceFB = IngredientSourceFB.getInstance(FirebaseFirestore.getInstance())
        serviceLocator.registerService(ingredientSourceFB)

        val userInfoSourceFB = UserInfoSourceFB.getInstance(FirebaseFirestore.getInstance())
        serviceLocator.registerService(userInfoSourceFB)

        val uriUtils = UriUtils.getInstance(this.applicationContext)
        serviceLocator.registerService(uriUtils)
    }

}