package com.pingwinek.jens.cookandbake.lib.sync

import org.junit.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito
import org.mockito.Mockito.mock
import java.util.*

class SynchManagerTest {

    private class TestLocal : ModelLocal {
        override val id = 1
        override val remoteId = 1
        override var lastModified = Date().time
    }

    private class TestRemote : Model {
        override val id = 1
        override var lastModified = Date().time
    }

    private class TestSynchManager(override val syncLogic: SyncLogic<TestLocal, TestRemote>) : SyncManager<TestLocal, TestRemote>() {

        var testResult: String? = null

        override fun newLocal(remote: TestRemote, onDone: () -> Unit) {
            testResult = "NewLocal"
        }

        override fun newRemote(local: TestLocal, onDone: () -> Unit) {
            testResult = "NewRemote"
        }

        override fun updateLocal(local: TestLocal, remote: TestRemote, onDone: () -> Unit) {
            testResult = "UpdateLocal"
        }

        override fun updateRemote(local: TestLocal, remote: TestRemote, onDone: () -> Unit) {
            testResult = "UpdateRemote"
        }

        override fun deleteLocal(local: TestLocal, onDone: () -> Unit) {
            testResult = "DeleteLocal"
        }

        override fun deleteRemote(remote: TestRemote, onDone: () -> Unit) {
            testResult = "DeleteRemote"
        }
    }

    @Suppress("Unchecked_Cast")
    private val mockedSyncLogic = mock(SyncLogic::class.java) as SyncLogic<TestLocal, TestRemote>
    private val testSynchManager = TestSynchManager(mockedSyncLogic)
    private val testLocal = TestLocal()
    private val testRemote = TestRemote()

    @Test
    fun testNewLocal() {
        Mockito.`when`(mockedSyncLogic.compare(any(), any())).thenReturn(SyncLogic.SyncAction.NEW_LOCAL)
        testSynchManager.sync(null, testRemote) {}
        assert(testSynchManager.testResult == "NewLocal")
    }

    @Test
    fun testNewRemote() {
        Mockito.`when`(mockedSyncLogic.compare(any(), any())).thenReturn(SyncLogic.SyncAction.NEW_REMOTE)
        testSynchManager.sync(testLocal, null) {}
        assert(testSynchManager.testResult == "NewRemote")
    }

    @Test
    fun testUpdateLocal() {
        Mockito.`when`(mockedSyncLogic.compare(any(), any())).thenReturn(SyncLogic.SyncAction.UPDATE_LOCAL)
        testSynchManager.sync(testLocal, testRemote) {}
        assert(testSynchManager.testResult == "UpdateLocal")
    }

    @Test
    fun testUpdateRemote() {
        Mockito.`when`(mockedSyncLogic.compare(any(), any())).thenReturn(SyncLogic.SyncAction.UPDATE_REMOTE)
        testSynchManager.sync(testLocal, testRemote) {}
        assert(testSynchManager.testResult == "UpdateRemote")
    }

    @Test
    fun testDeleteLocal() {
        Mockito.`when`(mockedSyncLogic.compare(any(), any())).thenReturn(SyncLogic.SyncAction.DELETE_LOCAL)
        testSynchManager.sync(testLocal, null) {}
        assert(testSynchManager.testResult == "DeleteLocal")
    }

    @Test
    fun testDeleteRemote() {
        Mockito.`when`(mockedSyncLogic.compare(any(), any())).thenReturn(SyncLogic.SyncAction.DELETE_REMOTE)
        testSynchManager.sync(null, testRemote) {}
        assert(testSynchManager.testResult == "DeleteRemote")
    }
}