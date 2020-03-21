package com.pingwinek.jens.cookandbake

import android.app.Application
import android.util.Log
import com.pingwinek.jens.cookandbake.lib.sync.SourceProvider
import com.pingwinek.jens.cookandbake.lib.sync.SyncService
import com.pingwinek.jens.cookandbake.lib.networkRequest.AbstractNetworkResponseRoutes
import com.pingwinek.jens.cookandbake.lib.networkRequest.GlobalNetworkResponseRoutes
import com.pingwinek.jens.cookandbake.sources.IngredientSourceLocal
import com.pingwinek.jens.cookandbake.sources.IngredientSourceRemote
import com.pingwinek.jens.cookandbake.sources.RecipeSourceLocal
import com.pingwinek.jens.cookandbake.sources.RecipeSourceRemote
import com.pingwinek.jens.cookandbake.sync.*

class PingwinekCooksApplication : Application() {

    private val tag = this::class.java.name

    override fun onCreate() {
        super.onCreate()

        // Global ResponseRoutings
        /*
        Code 401 signals invalid session, e.g. due to missing or invalid cookie;
        intended behavior is to try authentication based on refresh token
         */
        GlobalNetworkResponseRoutes.registerGlobalResponseRoute(
            AbstractNetworkResponseRoutes.Result.SUCCESS, 401
        ) { result, code, response, request ->
            Log.i(tag, "defaultRoute for 401")
        }

        val syncService = SyncService.getInstance(this)
        syncService.registerSyncManager(IngredientSyncManager(IngredientSyncLogic(), this))
        syncService.registerSyncManager(RecipeSyncManager(RecipeSyncLogic(), this))

        SourceProvider.registerLocalSource(RecipeSourceLocal.getInstance(this))
        SourceProvider.registerLocalSource(IngredientSourceLocal.getInstance(this))
        SourceProvider.registerRemoteSource(RecipeSourceRemote.getInstance(this))
        SourceProvider.registerRemoteSource(IngredientSourceRemote.getInstance(this))
    }

}