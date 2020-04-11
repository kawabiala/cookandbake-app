package com.pingwinek.jens.cookandbake

import android.app.Application
import android.util.Log
import com.pingwinek.jens.cookandbake.db.DatabaseService
import com.pingwinek.jens.cookandbake.lib.InternetConnectivityManager
import com.pingwinek.jens.cookandbake.lib.ServiceLocator
import com.pingwinek.jens.cookandbake.lib.sync.SyncService
import com.pingwinek.jens.cookandbake.lib.networkRequest.AbstractNetworkResponseRoutes
import com.pingwinek.jens.cookandbake.lib.networkRequest.GlobalNetworkResponseRoutes
import com.pingwinek.jens.cookandbake.lib.networkRequest.NetworkRequestProvider
import com.pingwinek.jens.cookandbake.sources.IngredientSourceLocal
import com.pingwinek.jens.cookandbake.sources.IngredientSourceRemote
import com.pingwinek.jens.cookandbake.sources.RecipeSourceLocal
import com.pingwinek.jens.cookandbake.sources.RecipeSourceRemote
import com.pingwinek.jens.cookandbake.sync.*
import org.json.JSONException
import org.json.JSONObject

class PingwinekCooksApplication : Application() {

    private val tag = this::class.java.name
    private val verificationError = "Verification error"

    private val serviceLocator = PingwinekCooksServiceLocator()

    override fun onCreate() {
        super.onCreate()

        registerServices()

        // Global ResponseRoutings
        /*
        Code 401 signals invalid session, e.g. due to missing or invalid cookie;
        intended behavior is to try authentication based on refresh token
         */
        GlobalNetworkResponseRoutes.registerGlobalResponseRoute(
            AbstractNetworkResponseRoutes.Result.SUCCESS, 401
        ) { _, _, _, _ ->
            Log.i(tag, "defaultRoute for 401")
        }

        /*
        Code 404 might signal, that we have a verification error. In case we get
        a verification error, we trigger logout.
         */
        GlobalNetworkResponseRoutes.registerGlobalResponseRoute(
            AbstractNetworkResponseRoutes.Result.SUCCESS, 404
        ) { _, _, response, _ ->
            try {
                val jsonObject = JSONObject(response)
                val error = jsonObject.getString("error")
                if (error == verificationError) {
                    getServiceLocator().getService(AuthService::class.java)?.logout { _, _ ->}
                }
            } finally {
                Log.e(tag, response)
            }
        }

    }

    fun getServiceLocator(): ServiceLocator {
        return serviceLocator
    }

    private fun registerServices() {
        val internetConnectivityManager = InternetConnectivityManager.getInstance(this)
        serviceLocator.registerService(internetConnectivityManager)

        val pingwinekCooksDB = DatabaseService.getDatabase(this)
        val networkRequestProvider = NetworkRequestProvider.getInstance(this)

        val authService = AuthService.getInstance(this)
        serviceLocator.registerService(authService)

        val retryManager = PingwinekCooksRetryManager(authService)

        val ingredientSourceLocal = IngredientSourceLocal.getInstance(pingwinekCooksDB)
        serviceLocator.registerService(ingredientSourceLocal)

        val ingredientSourceRemote = IngredientSourceRemote.getInstance(networkRequestProvider)
        ingredientSourceRemote.retryManager = retryManager
        serviceLocator.registerService(ingredientSourceRemote)

        val recipeSourceLocal = RecipeSourceLocal.getInstance(pingwinekCooksDB)
        serviceLocator.registerService(recipeSourceLocal)

        val recipeSourceRemote = RecipeSourceRemote.getInstance(networkRequestProvider)
        recipeSourceRemote.retryManager = retryManager
        serviceLocator.registerService(recipeSourceRemote)

        val syncService = SyncService.getInstance(internetConnectivityManager)
        syncService.registerSyncManager(IngredientSyncManager(
            recipeSourceLocal,
            ingredientSourceLocal,
            ingredientSourceRemote,
            IngredientSyncLogic())
        )
        syncService.registerSyncManager(RecipeSyncManager(
            recipeSourceLocal,
            recipeSourceRemote,
            RecipeSyncLogic())
        )
        serviceLocator.registerService(syncService)
    }

}