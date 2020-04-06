package com.pingwinek.jens.cookandbake.lib.sync

import com.nhaarman.mockitokotlin2.*
import org.junit.Test
import java.util.*

class SyncHelperTest {

    val local = mock<ModelLocal>()
    val remote = mock<Model>()

    val testSyncManager = mock<SyncManager<ModelLocal, Model>>()

    val syncHelper = SyncHelper(testSyncManager) {}

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