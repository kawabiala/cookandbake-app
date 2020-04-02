package com.pingwinek.jens.cookandbake.lib.sync

import android.app.Application
import com.pingwinek.jens.cookandbake.models.*
import com.pingwinek.jens.cookandbake.sources.IngredientSource
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.*
import java.util.*

class SyncServiceTest {
/*
    private val application = mock(Application::class.java)
    private val syncService = SyncService.getInstance(application)

    @Suppress("Unchecked_Cast")
    private val mockedIngredientSyncLogic = mock(SyncLogic::class.java) as SyncLogic<IngredientLocal, IngredientRemote>

    private val syncManager = object :
        SyncManager<IngredientLocal, IngredientRemote>() {
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
        override fun getAll() : Promise<LinkedList<IngredientLocal>> {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun get(id: Int) : Promise<IngredientLocal> {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun new(item: IngredientLocal) : Promise<IngredientLocal> {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun update(item: IngredientLocal) : Promise<IngredientLocal> {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun delete(id: Int) : Promise<Unit> {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun getAllForRecipeId(recipeId: Int) : Promise<LinkedList<IngredientLocal>> {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

    }

    private class MockedIngredientSourceLocalSuccess : MockedIngredientSourceLocal() {
        override fun getAll(): Promise<LinkedList<IngredientLocal>> {
            return Promise<LinkedList<IngredientLocal>>().apply {
                setResult(
                    Promise.Status.SUCCESS,
                    LinkedList<IngredientLocal>().apply {
                        add(IngredientLocal(1, 2, 1, null, null, "Ingredient Local 1"))
                    }
                )
            }

        }

        override fun get(id: Int) : Promise<IngredientLocal> {
            return Promise<IngredientLocal>().apply {
                setResult(
                    Promise.Status.SUCCESS,
                    IngredientLocal(1, 2, 1, null, null, "Ingredient Local 1")
                )
            }
        }
    }

    private class MockedIngredientSourceLocalFailure : MockedIngredientSourceLocal() {
        override fun getAll() : Promise<LinkedList<IngredientLocal>> {
            return Promise<LinkedList<IngredientLocal>>().apply {
                setResult(
                    Promise.Status.FAILURE,
                    LinkedList()
                )
            }
        }

        override fun get(id: Int) : Promise<IngredientLocal> {
            return Promise<IngredientLocal>().apply {
                setResult(
                    Promise.Status.FAILURE,
                    null
                )
            }
        }
    }

    private abstract class MockedIngredientSourceRemote : IngredientSource<IngredientRemote> {
        override fun getAll() : Promise<LinkedList<IngredientRemote>> {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun get(id: Int) : Promise<IngredientRemote> {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun new(item: IngredientRemote) : Promise<IngredientRemote> {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun update(item: IngredientRemote) : Promise<IngredientRemote> {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun delete(id: Int) : Promise<Unit> {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun getAllForRecipeId(recipeId: Int) : Promise<LinkedList<IngredientRemote>> {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

    }

    private class MockedIngredientSourceRemoteSuccess : MockedIngredientSourceRemote() {
        override fun getAll() : Promise<LinkedList<IngredientRemote>> {
            return Promise<LinkedList<IngredientRemote>>().apply {
                setResult(
                    Promise.Status.SUCCESS,
                    LinkedList<IngredientRemote>().apply {
                        add(IngredientRemote.fromLocal(
                            IngredientLocal(0, 2, 1, null, null, "Ingredient Remote 2"), 3
                        ))
                    }
                )
            }
        }

        override fun get(id: Int) : Promise<IngredientRemote> {
            return Promise<IngredientRemote>().apply {
                setResult(
                    Promise.Status.SUCCESS,
                    IngredientRemote.fromLocal(
                        IngredientLocal(0, 2, 1, null, null, "Ingredient Remote 2"), 3)
                )
            }
        }

    }

    private class MockedIngredientSourceRemoteFailure : MockedIngredientSourceRemote() {
        override fun getAll() : Promise<LinkedList<IngredientRemote>> {
            return Promise<LinkedList<IngredientRemote>>().apply {
                setResult(
                    Promise.Status.FAILURE,
                    LinkedList()
                )
            }
        }

        override fun get(id: Int) : Promise<IngredientRemote> {
            return Promise<IngredientRemote>().apply {
                setResult(
                    Promise.Status.FAILURE,
                    null
                )
            }
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
        assert(syncService.getSyncManager<IngredientLocal, IngredientRemote>() is SyncManager<IngredientLocal, IngredientRemote>)
    }

    @Test
    fun syncEntry1() {
        println("Test syncEntry 1")

        withoutSyncManager {
            syncService.syncEntry<IngredientLocal, IngredientRemote>(null, null, it)
        }

        happyCase {
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

        happyCase {
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

        happyCase {
            syncService.sync<IngredientLocal, IngredientRemote> {}
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

        happyCase {
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
        println(("without Sources"))
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
        test { onDoneCalled = true }
        assert(onDoneCalled)
    }

    private fun clear() {
        syncService.removeAllSyncManagers()
        SourceProvider.removeSources()
    }

 */
}