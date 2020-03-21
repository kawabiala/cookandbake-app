package com.pingwinek.jens.cookandbake

import android.app.Application
import com.pingwinek.jens.cookandbake.lib.sync.SourceProvider
import com.pingwinek.jens.cookandbake.lib.sync.SyncLogic
import com.pingwinek.jens.cookandbake.lib.sync.SyncService
import com.pingwinek.jens.cookandbake.lib.sync.SynchManager
import com.pingwinek.jens.cookandbake.models.*
import com.pingwinek.jens.cookandbake.sources.IngredientSource
import com.pingwinek.jens.cookandbake.lib.sync.Source
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.*
import java.util.*

class SyncServiceTest {

    private val application = mock(Application::class.java)
    private val syncService = SyncService.getInstance(application)

    private val mockedIngredientSyncLogic = mock(SyncLogic::class.java) as SyncLogic<IngredientLocal, IngredientRemote>

    private val syncManager = object :
        SynchManager<IngredientLocal, IngredientRemote> {
        override val syncLogic: SyncLogic<IngredientLocal, IngredientRemote>
            get() = mockedIngredientSyncLogic

        override fun newLocal(remote: IngredientRemote, onDone: () -> Unit) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun newRemote(local: IngredientLocal, onDone: () -> Unit) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun updateLocal(
            local: IngredientLocal,
            remote: IngredientRemote,
            onDone: () -> Unit
        ) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun updateRemote(
            local: IngredientLocal,
            remote: IngredientRemote,
            onDone: () -> Unit
        ) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun deleteLocal(local: IngredientLocal, onDone: () -> Unit) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun deleteRemote(remote: IngredientRemote, onDone: () -> Unit) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

    }

    private abstract class MockedIngredientSourceLocal : IngredientSource<IngredientLocal> {
        override fun getAll(callback: (Source.Status, LinkedList<IngredientLocal>) -> Unit) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun get(id: Int, callback: (Source.Status, IngredientLocal?) -> Unit) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun new(
            item: IngredientLocal,
            callback: (Source.Status, IngredientLocal?) -> Unit
        ) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun update(
            item: IngredientLocal,
            callback: (Source.Status, IngredientLocal?) -> Unit
        ) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun delete(id: Int, callback: (Source.Status) -> Unit) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun getAllForRecipeId(
            recipeId: Int,
            callback: (Source.Status, LinkedList<IngredientLocal>) -> Unit
        ) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

    }

    private class MockedIngredientSourceLocalSuccess : MockedIngredientSourceLocal() {
        override fun getAll(callback: (Source.Status, LinkedList<IngredientLocal>) -> Unit) {
            callback(
                Source.Status.SUCCESS,
                LinkedList<IngredientLocal>().apply {
                    add(IngredientLocal(1, 2, 1, null, null, "Ingredient Local 1"))
                }
            )
        }

        override fun get(id: Int, callback: (Source.Status, IngredientLocal?) -> Unit) {
            callback(
                Source.Status.SUCCESS,
                IngredientLocal(1, 2, 1, null, null, "Ingredient Local 1")
            )
        }
    }

    private class MockedIngredientSourceLocalFailure : MockedIngredientSourceLocal() {
        override fun getAll(callback: (Source.Status, LinkedList<IngredientLocal>) -> Unit) {
            callback(
                Source.Status.FAILURE,
                LinkedList<IngredientLocal>()
            )
        }

        override fun get(id: Int, callback: (Source.Status, IngredientLocal?) -> Unit) {
            callback(
                Source.Status.FAILURE,
                null
            )
        }
    }

    private abstract class MockedIngredientSourceRemote : IngredientSource<IngredientRemote> {
        override fun getAll(callback: (Source.Status, LinkedList<IngredientRemote>) -> Unit) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun get(id: Int, callback: (Source.Status, IngredientRemote?) -> Unit) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun new(
            item: IngredientRemote,
            callback: (Source.Status, IngredientRemote?) -> Unit
        ) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun update(
            item: IngredientRemote,
            callback: (Source.Status, IngredientRemote?) -> Unit
        ) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun delete(id: Int, callback: (Source.Status) -> Unit) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun getAllForRecipeId(
            recipeId: Int,
            callback: (Source.Status, LinkedList<IngredientRemote>) -> Unit
        ) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

    }

    private class MockedIngredientSourceRemoteSuccess : MockedIngredientSourceRemote() {
        override fun getAll(callback: (Source.Status, LinkedList<IngredientRemote>) -> Unit) {
            callback(
                Source.Status.SUCCESS,
                LinkedList<IngredientRemote>().apply {
                    add(IngredientRemote.fromLocal(
                        IngredientLocal(0, 2, 1, null, null, "Ingredient Remote 2"), 3
                    ))
                }
            )
        }

        override fun get(id: Int, callback: (Source.Status, IngredientRemote?) -> Unit) {
            callback(
                Source.Status.SUCCESS,
                IngredientRemote.fromLocal(
                    IngredientLocal(0, 2, 1, null, null, "Ingredient Remote 2"), 3)
            )
        }

    }

    private class MockedIngredientSourceRemoteFailure : MockedIngredientSourceRemote() {
        override fun getAll(callback: (Source.Status, LinkedList<IngredientRemote>) -> Unit) {
            callback(
                Source.Status.FAILURE,
                LinkedList<IngredientRemote>()
            )
        }

        override fun get(id: Int, callback: (Source.Status, IngredientRemote?) -> Unit) {
            callback(
                Source.Status.FAILURE,
                null
            )
        }

    }

    @Before
    fun setup() {
        `when`(mockedIngredientSyncLogic.compare(ArgumentMatchers.any(), ArgumentMatchers.any()))
            .thenReturn(SyncLogic.SyncAction.DO_NOTHING)
    }

    @Test
    fun getSyncManager() {
        println("Test getSyncManager")

        prepareHappyCase()
        assert(syncService.getSyncManager<IngredientLocal, IngredientRemote>() is SynchManager<IngredientLocal, IngredientRemote>)
    }

    @Test
    fun syncEntry1() {
        println("Test syncEntry 1")

        withoutSyncManager {
            syncService.syncEntry<IngredientLocal, IngredientRemote>(null, null, it)
        }

        happyCase() {
            syncService.syncEntry<IngredientLocal, IngredientRemote>(null, null) {}
        }
    }

    @Test
    fun syncEntry2() {
        println("Test syncEntry 2")

        withoutSyncManager {
            syncService.syncEntry<IngredientLocal, IngredientRemote>(1, it)
        }

        withoutSources {
            syncService.syncEntry<IngredientLocal, IngredientRemote>(1, it)
        }

        withOneSource {
            syncService.syncEntry<IngredientLocal, IngredientRemote>(1, it)
        }

        withFailureFromLocalSource {
            syncService.syncEntry<IngredientLocal, IngredientRemote>(1, it)
        }

        withFailureFromRemoteSource {
            syncService.syncEntry<IngredientLocal, IngredientRemote>(1, it)
        }

        happyCase() {
            syncService.syncEntry<IngredientLocal, IngredientRemote>(1) {}
        }
    }

    /*
    We want to test that the callback onDone is called in any case. This indicates also,
    that getAll is called for local and remote source, if we provide these sources.

    We also want to check, that synchronisation is really invoked, if everything is provided
     */
    @Test
    fun sync() {
        println("Test sync")

        withoutSyncManager {
            syncService.sync<IngredientLocal, IngredientRemote>(it)
        }

        withoutSources {
            syncService.sync<IngredientLocal, IngredientRemote>(it)
        }

        withOneSource {
            syncService.sync<IngredientLocal, IngredientRemote>(it)
        }

        withFailureFromLocalSource {
            syncService.sync<IngredientLocal, IngredientRemote>(it)
        }

        withFailureFromRemoteSource {
            syncService.sync<IngredientLocal, IngredientRemote>(it)
        }

        happyCase() {
            syncService.sync<IngredientLocal, IngredientRemote>() {}
        }
    }

    @Test
    fun syncAll() {
        println("Test syncAll")

        withoutSyncManager {
            syncService.syncAll(it)
        }

        withoutSources {
            syncService.syncAll(it)
        }

        withOneSource {
            syncService.syncAll(it)
        }

        withFailureFromLocalSource {
            syncService.syncAll(it)
        }

        withFailureFromRemoteSource {
            syncService.syncAll(it)
        }

        happyCase() {
            syncService.syncAll {}
        }
        syncService.syncAll {}
    }

    private fun withoutSyncManager(test: (onDone: () -> Unit) -> Unit) {
        println(("without SyncManager"))
        clear()

        doTest(test)
    }

    private fun withoutSources(test: (onDone: () -> Unit) -> Unit) {
        System.out.println(("without Sources"))
        clear()
        syncService.registerSyncManager(syncManager)

        doTest(test)
    }

    private fun withOneSource(test: (onDone: () -> Unit) -> Unit) {
        println(("with one Source"))
        clear()
        syncService.registerSyncManager(syncManager)
        SourceProvider.registerLocalSource(MockedIngredientSourceLocalSuccess())

        doTest(test)
    }

    private fun withFailureFromLocalSource(test: (onDone: () -> Unit) -> Unit) {
        println(("with Failure from local Source"))
        clear()
        syncService.registerSyncManager(syncManager)
        SourceProvider.registerLocalSource(MockedIngredientSourceLocalFailure())
        SourceProvider.registerRemoteSource(MockedIngredientSourceRemoteSuccess())

        doTest(test)
    }

    private fun withFailureFromRemoteSource(test: (onDone: () -> Unit) -> Unit) {
        println(("with Failure from remote Source"))
        clear()
        syncService.registerSyncManager(syncManager)
        SourceProvider.registerLocalSource(MockedIngredientSourceLocalSuccess())
        SourceProvider.registerRemoteSource(MockedIngredientSourceRemoteFailure())

        doTest(test)
    }

    private fun prepareHappyCase() {
        clear()
        syncService.registerSyncManager(syncManager)
        SourceProvider.registerLocalSource(MockedIngredientSourceLocalSuccess())
        SourceProvider.registerRemoteSource(MockedIngredientSourceRemoteSuccess())
    }

    private fun happyCase(test: () -> Unit) {
        println(("happy case"))
        prepareHappyCase()

        test()
        verify(syncService.getSyncManager<IngredientLocal, IngredientRemote>()?.syncLogic)
            ?.compare(ArgumentMatchers.any(), ArgumentMatchers.any())
    }

    private fun doTest(test: (onDone: () -> Unit) -> Unit) {
        var onDoneCalled = false
        test() { onDoneCalled = true }
        assert(onDoneCalled)
    }

    private fun clear() {
        syncService.removeAllSyncManagers()
        SourceProvider.removeSources()
    }
}