package com.pingwinek.jens.cookandbake.lib.sync

import android.app.Application
import com.nhaarman.mockitokotlin2.*
import com.pingwinek.jens.cookandbake.TestUtils
import com.pingwinek.jens.cookandbake.lib.networkRequest.InternetConnectivityManager
import org.junit.Before
import org.junit.Test

class SyncServiceTest {

    private val mockedInternetConnectivityManager = mock<InternetConnectivityManager>()
    private val testLocal = mock<ModelLocal>()
    private val testRemote = mock<Model>()

    init {
        TestUtils.mockSingletonHolderInstance(InternetConnectivityManager::class, mockedInternetConnectivityManager)
        doNothing().whenever(mockedInternetConnectivityManager).registerNetworkCallback(any())
    }

    private val testSyncManager = mock<SyncManager<ModelLocal, Model>>()
    private val testSyncService = SyncService.getInstance(mock<Application>())

    @Before
    fun setup() {
        testSyncService.removeAllSyncManagers()
    }

    @Test
    fun testRegisterSyncManager() {
        testSyncService.registerSyncManager(testSyncManager)
        assert(testSyncService.getSyncManager<ModelLocal, Model>() == testSyncManager)
        assert(testSyncService.getSyncManager(ModelLocal::class.java, Model::class.java) == testSyncManager)

        testSyncService.removeSyncManager<ModelLocal, Model>()
        assert(testSyncService.getSyncManager<ModelLocal, Model>() == null)
        assert(testSyncService.getSyncManager(ModelLocal::class.java, Model::class.java) == null)

        testSyncService.registerSyncManager(testSyncManager)
        assert(testSyncService.getSyncManager<ModelLocal, Model>() == testSyncManager)
        assert(testSyncService.getSyncManager(ModelLocal::class.java, Model::class.java) == testSyncManager)

        testSyncService.removeAllSyncManagers()
        assert(testSyncService.getSyncManager<ModelLocal, Model>() == null)
        assert(testSyncService.getSyncManager(ModelLocal::class.java, Model::class.java) == null)
    }

    @Test
    fun testSync() {
        testSyncService.registerSyncManager(testSyncManager)
        testSyncService.syncEntry(testLocal, testRemote) {}
        verify(testSyncManager).syncEntry(any<ModelLocal>(), any<Model>(), anyOrNull())

        testSyncService.syncEntry<ModelLocal, Model>(1) {}
        verify(testSyncManager).syncEntry(any<Int>(), anyOrNull())

        testSyncService.syncByParentId<ModelLocal, Model>(1) {}
        verify(testSyncManager).syncByParentId(any<Int>(), anyOrNull())

        testSyncService.sync<ModelLocal, Model> {}
        verify(testSyncManager).sync(anyOrNull())
    }

    @Test
    fun testSyncAll() {
        testSyncService.registerSyncManager(testSyncManager)
        testSyncService.syncAll {}
        verify(testSyncManager).sync(anyOrNull())
    }
}