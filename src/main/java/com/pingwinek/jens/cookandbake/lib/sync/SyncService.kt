package com.pingwinek.jens.cookandbake.lib.sync

import android.net.ConnectivityManager
import android.net.Network
import com.pingwinek.jens.cookandbake.lib.InternetConnectivityManager
import com.pingwinek.jens.cookandbake.utils.SingletonHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class SyncService private constructor(internetConnectivityManager: InternetConnectivityManager) {

    val syncManagerIdentifiers = LinkedList<SyncManagerIdentifier<ModelLocal, Model>>()

    private val netWorkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            CoroutineScope(Dispatchers.IO).launch {
                syncAll()
            }
        }
    }

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

    suspend inline fun <reified TLocal: ModelLocal, reified TRemote: Model> syncEntry(
        local: TLocal?,
        remote: TRemote?
    ) {
        getSyncManager<TLocal, TRemote>()?.syncEntry(local, remote)
    }

    suspend inline fun <reified TLocal: ModelLocal, reified TRemote: Model>syncEntry(
        localId: Int
    ) {
        getSyncManager<TLocal, TRemote>()?.syncEntry(localId)
    }

    suspend inline fun <reified TLocal: ModelLocal, reified TRemote: Model> sync(
            locals: List<TLocal>,
            remotes: List<TRemote>
    ) {
        getSyncManager<TLocal, TRemote>()?.sync(locals, remotes)
    }

    suspend inline fun <reified TLocal: ModelLocal, reified TRemote: Model> syncByParentId(
        localParentId: Int
    ) {
        getSyncManager<TLocal, TRemote>()?.syncByParentId(localParentId)
    }

    suspend inline fun <reified TLocal: ModelLocal, reified TRemote: Model> sync() {
        getSyncManager<TLocal, TRemote>()?.sync()
    }

    suspend fun syncAll() {
        syncManagerIdentifiers.forEach { syncManagerIdentifier ->
            val syncManager = syncManagerIdentifier.syncManager
            syncManager.sync()
        }
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

    companion object : SingletonHolder<SyncService, InternetConnectivityManager>(::SyncService)
}