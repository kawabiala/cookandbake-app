package com.pingwinek.jens.cookandbake.lib.sync

import android.app.Application
import android.net.ConnectivityManager
import android.net.Network
import com.pingwinek.jens.cookandbake.lib.networkRequest.InternetConnectivityManager
import com.pingwinek.jens.cookandbake.utils.CallbackLoopCounter
import com.pingwinek.jens.cookandbake.utils.SingletonHolder
import java.util.*

class SyncService private constructor(val application: Application) {

    val syncManagerIdentifiers = LinkedList<SyncManagerIdentifier<ModelLocal, Model>>()

    private val netWorkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network?) {
            syncAll {}
        }
    }

    private val internetConnectivityManager = InternetConnectivityManager.getInstance(application)

    init {
        internetConnectivityManager.registerNetworkCallback(netWorkCallback)
    }

    @Suppress("Unchecked_Cast")
    inline fun <reified TLocal: ModelLocal, reified TRemote: Model>registerSyncManager(syncManager: SyncManager<TLocal, TRemote>) {
        syncManagerIdentifiers.add(
            SyncManagerIdentifier(
                TLocal::class.java as Class<ModelLocal>,
                TRemote::class.java as Class<Model>,
                syncManager as SyncManager<ModelLocal, Model>
            )
        )
    }

    inline fun <reified TLocal: ModelLocal, reified TRemote: Model>getSyncManager() : SyncManager<TLocal, TRemote>? {
        return getSyncManager(TLocal::class.java, TRemote::class.java)
    }

    @Suppress("Unchecked_Cast")
    fun <TLocal: ModelLocal, TRemote: Model> getSyncManager(
        localClass: Class<TLocal>,
        remoteClass: Class<TRemote>
    ) : SyncManager<TLocal, TRemote>? {
        val syncManagerIdentifier = syncManagerIdentifiers.find { syncManagerIdentifier ->
            syncManagerIdentifier.isSuitableFor(localClass, remoteClass)
        }
        return if (syncManagerIdentifier != null) {
            syncManagerIdentifier.syncManager as SyncManager<TLocal, TRemote>
        } else {
            null
        }
    }

    @Suppress("Unused")
    inline fun <reified TLocal: ModelLocal, reified TRemote: Model>removeSyncManager() {
        syncManagerIdentifiers.removeAll {
            it.isSuitableFor(TLocal::class.java, TRemote::class.java)
        }
    }

    fun removeAllSyncManagers() {
        syncManagerIdentifiers.clear()
    }

    inline fun <reified TLocal: ModelLocal, reified TRemote: Model> syncEntry(
        local: TLocal?,
        remote: TRemote?,
        noinline onDone: () -> Unit
    ) {
        getSyncManager<TLocal, TRemote>()?.syncEntry(local, remote, onDone)
    }

    inline fun <reified TLocal: ModelLocal, reified TRemote: Model>syncEntry(
        localId: Int,
        noinline onDone: () -> Unit
    ) {
        getSyncManager<TLocal, TRemote>()?.syncEntry(localId, onDone)
    }

    inline fun <reified TLocal: ModelLocal, reified TRemote: Model> syncByParentId(
        localParentId: Int,
        noinline onDone: () -> Unit
    ) {
        getSyncManager<TLocal, TRemote>()?.syncByParentId(localParentId, onDone)
    }

    inline fun <reified TLocal: ModelLocal, reified TRemote: Model> sync(
        noinline onDone: () -> Unit
    ) {
        getSyncManager<TLocal, TRemote>()?.sync(onDone)
    }

    fun syncAll(onDone: () -> Unit) {
        val syncTaskCounter = CallbackLoopCounter(onDone)
        syncTaskCounter.taskStarted()
        syncManagerIdentifiers.forEach { syncManagerIdentifier ->
            syncTaskCounter.taskStarted()
            val syncManager = syncManagerIdentifier.syncManager
            syncManager.sync { syncTaskCounter.taskEnded() }

        }
        syncTaskCounter.taskEnded()
    }

    class SyncManagerIdentifier<TLocal: ModelLocal, TRemote: Model>(
        private val localClass: Class<ModelLocal>,
        private val remoteClass: Class<Model>,
        val syncManager: SyncManager<TLocal, TRemote>
    ) {
        fun isSuitableFor(localClass: Class<*>, remoteClass: Class<*>) : Boolean {
            return (localClass == this.localClass && remoteClass == this.remoteClass)
        }
    }

    companion object : SingletonHolder<SyncService, Application>(::SyncService)
}