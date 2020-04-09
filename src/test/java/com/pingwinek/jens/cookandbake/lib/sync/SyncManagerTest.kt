package com.pingwinek.jens.cookandbake.lib.sync

import com.nhaarman.mockitokotlin2.*
import org.junit.Test
import java.util.*

/**
 * Tests for abstract class [SyncManager], testing its none abstract methods
 *
 */
class SyncManagerTest {

    /**
     * Implementation of [ModelLocal] with static assignment of id and remoteId
     * TODO: A mock would look nicer
     */
    private class TestLocal : ModelLocal {
        override val id = 1
        override val remoteId = 1
        override var lastModified = Date().time
    }

    /**
     * Implementation of [Model] with static assignement of id, where id is same as remoteId of TestLocal
     * TODO: A mock would look nicer
     *
     */
    private class TestRemote : Model {
        override val id = 1
        override var lastModified = Date().time
    }

    /**
     * Pro forma implementation of abstract class [SyncManager]
     *
     * @constructor
     * Constructor
     *
     * @param testSourceLocal Mock of [SourceLocal] is sufficient, but needs a concrete type for [ModelLocal]
     * @param testSourceRemote Mock of [Source] is sufficient, but needs a concrete type for [Model]
     * @param syncLogic Mock of [SyncLogic] with same concrete types for [ModelLocal] and [Model] as used for testSourceLocal and testSourceRemote
     */
    private class TestSyncManager(
        testSourceLocal: SourceLocal<TestLocal>,
        testSourceRemote: Source<TestRemote>,
        syncLogic: SyncLogic<TestLocal, TestRemote>
    ) : SyncManager<TestLocal, TestRemote>(testSourceLocal, testSourceRemote, syncLogic) {

        override fun getLocalParent(parentId: Int): Promise<ModelLocal> {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun getLocalsByParent(parentId: Int): Promise<LinkedList<TestLocal>> {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun getRemotesByParent(parentId: Int): Promise<LinkedList<TestRemote>> {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun newLocal(remote: TestRemote, onDone: () -> Unit) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun newRemote(local: TestLocal, onDone: () -> Unit) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun updateLocal(local: TestLocal, remote: TestRemote, onDone: () -> Unit) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun updateRemote(local: TestLocal, remote: TestRemote, onDone: () -> Unit) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun deleteLocal(local: TestLocal, onDone: () -> Unit) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun deleteRemote(remote: TestRemote, onDone: () -> Unit) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }

    @Suppress("Unchecked_Cast")
    private val mockedSyncLogic = mock<SyncLogic<TestLocal, TestRemote>>()
    @Suppress("Unchecked_Cast")
    private val testSourceLocal = mock<SourceLocal<TestLocal>>()
    @Suppress("Unchecked_Cast")
    private val testSourceRemote = mock<Source<TestRemote>>()
    private val testSyncManager = spy(TestSyncManager(testSourceLocal, testSourceRemote, mockedSyncLogic))
    private val testLocal = TestLocal()
    private val testRemote = TestRemote()

    /**
     * First, we test syncEntry(local: TLocal?, remote: TRemote?, onDone: () -> Unit); the action to be taken,
     * is defined by mockedSyncLogic, so we just test, that the expected method is called.
     *
     *
     */
    @Test
    fun testNewLocal() {
        whenever(mockedSyncLogic.compare(isNull(), any())).thenReturn(SyncLogic.SyncAction.NEW_LOCAL)
        doNothing().whenever(testSyncManager).newLocal(any(), anyOrNull())
        testSyncManager.syncEntry(null, testRemote) {}
        verify(testSyncManager).newLocal(any(), anyOrNull())
    }

    @Test
    fun testNewRemote() {
        whenever(mockedSyncLogic.compare(any(), isNull())).thenReturn(SyncLogic.SyncAction.NEW_REMOTE)
        doNothing().whenever(testSyncManager).newRemote(any(), anyOrNull())
        testSyncManager.syncEntry(testLocal, null) {}
        verify(testSyncManager).newRemote(any(), anyOrNull())
    }

    @Test
    fun testUpdateLocal() {
        whenever(mockedSyncLogic.compare(any(), any())).thenReturn(SyncLogic.SyncAction.UPDATE_LOCAL)
        doNothing().whenever(testSyncManager).updateLocal(any(), any(), anyOrNull())
        testSyncManager.syncEntry(testLocal, testRemote) {}
        verify(testSyncManager).updateLocal(any(), any(), anyOrNull())
    }

    @Test
    fun testUpdateRemote() {
        whenever(mockedSyncLogic.compare(any(), any())).thenReturn(SyncLogic.SyncAction.UPDATE_REMOTE)
        doNothing().whenever(testSyncManager).updateRemote(any(), any(), anyOrNull())
        testSyncManager.syncEntry(testLocal, testRemote) {}
        verify(testSyncManager).updateRemote(any(), any(), anyOrNull())
    }

    @Test
    fun testDeleteLocal() {
        whenever(mockedSyncLogic.compare(any(), isNull())).thenReturn(SyncLogic.SyncAction.DELETE_LOCAL)
        doNothing().whenever(testSyncManager).deleteLocal(any(), anyOrNull())
        testSyncManager.syncEntry(testLocal, null) {}
        verify(testSyncManager).deleteLocal(any(), anyOrNull())
    }

    @Test
    fun testDeleteRemote() {
        whenever(mockedSyncLogic.compare(isNull(), any())).thenReturn(SyncLogic.SyncAction.DELETE_REMOTE)
        doNothing().whenever(testSyncManager).deleteRemote(any(), anyOrNull())
        testSyncManager.syncEntry(null, testRemote) {}
        verify(testSyncManager).deleteRemote(any(), anyOrNull())
    }

    /**
     * Second, we test the other sync-Methods and check, that call the expected internal methods and
     * finally the basic syncEntry-method, that we've already tested.
     *
     */
    @Test
    fun testSyncEntry() {
        val localPromise = Promise<TestLocal>().apply {
            setResult(Promise.Status.SUCCESS, testLocal)
        }
        val remotePromise = Promise<TestRemote>().apply {
            setResult(Promise.Status.SUCCESS, testRemote)
        }
        doReturn(localPromise).whenever(testSyncManager).getLocal(any<Int>())
        doReturn(remotePromise).whenever(testSyncManager).getRemote(any<TestLocal>())
        doNothing().whenever(testSyncManager).syncEntry(any(), any(), anyOrNull())
        testSyncManager.syncEntry(1) {}
        inOrder(testSyncManager) {
            verify(testSyncManager).getLocal(any<Int>())
            verify(testSyncManager).getRemote(any<TestLocal>())
            verify(testSyncManager).syncEntry(any(), any(), anyOrNull())
        }
    }

    @Test
    fun testSyncByParentId() {
        val localListPromise = Promise<LinkedList<TestLocal>>().apply {
            setResult(Promise.Status.SUCCESS, LinkedList<TestLocal>().apply {
                add(testLocal)
            })
        }
        val localPromise = Promise<TestLocal>().apply {
            setResult(Promise.Status.SUCCESS, testLocal)
        }
        val remoteListPromise = Promise<LinkedList<TestRemote>>().apply {
            setResult(Promise.Status.SUCCESS, LinkedList<TestRemote>().apply {
                add(testRemote)
            })
        }
        doReturn(localListPromise).whenever(testSyncManager).getLocalsByParent(any())
        doReturn(localPromise).whenever(testSyncManager).getLocalParent(any())
        doReturn(remoteListPromise).whenever(testSyncManager).getRemotesByParent(any())
        doNothing().whenever(testSyncManager).syncEntry(any(), any(), anyOrNull())
        testSyncManager.syncByParentId(1) {}
        inOrder(testSyncManager) {
            verify(testSyncManager).getLocalsByParent(any())
            verify(testSyncManager).getLocalParent(any())
            verify(testSyncManager).getRemotesByParent(any())
            verify(testSyncManager).syncEntry(any(), any(), anyOrNull())
        }
    }

    @Test
    fun testSync() {
        val localListPromise = Promise<LinkedList<TestLocal>>().apply {
            setResult(Promise.Status.SUCCESS, LinkedList<TestLocal>().apply {
                add(testLocal)
            })
        }
        val remoteListPromise = Promise<LinkedList<TestRemote>>().apply {
            setResult(Promise.Status.SUCCESS, LinkedList<TestRemote>().apply {
                add(testRemote)
            })
        }
        doReturn(localListPromise).whenever(testSyncManager).getLocals()
        doReturn(remoteListPromise).whenever(testSyncManager).getRemotes()
        doNothing().whenever(testSyncManager).syncEntry(any(), any(), anyOrNull())
        testSyncManager.sync {}
        inOrder(testSyncManager) {
            verify(testSyncManager).getLocals()
            verify(testSyncManager).getRemotes()
            verify(testSyncManager).syncEntry(any(), any(), anyOrNull())
        }
    }
}