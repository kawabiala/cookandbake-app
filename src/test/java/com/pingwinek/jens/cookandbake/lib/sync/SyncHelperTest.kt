package com.pingwinek.jens.cookandbake.lib.sync

import com.nhaarman.mockitokotlin2.*
import org.junit.Test
import java.util.*

class SyncHelperTest {

    private val local = mock<ModelLocal>()
    private val remote = mock<Model>()

    private val testSyncManager = mock<SyncManager<ModelLocal, Model>>()

    private val syncHelper = SyncHelper(testSyncManager) {}

    @Test
    fun testSetLocalList() {
        doNothing().whenever(testSyncManager).syncEntry(any(), any(), anyOrNull())
        syncHelper.setLocalList(LinkedList<ModelLocal>().apply {
            add(local)
        })
        syncHelper.setRemoteList(LinkedList<Model>().apply {
            add(remote)
        })
        verify(testSyncManager).syncEntry(any(), any(), anyOrNull())
    }
}