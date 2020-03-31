package com.pingwinek.jens.cookandbake.lib.sync

import com.pingwinek.jens.cookandbake.models.IngredientLocal
import com.pingwinek.jens.cookandbake.models.IngredientRemote
import org.junit.Test

import org.junit.Before
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.mockito.Mockito.never
import java.util.*

class SyncHelperTest {

    @Suppress("Unchecked_Cast")
    private val mockedIngredientSyncLogic = Mockito.mock(SyncLogic::class.java) as SyncLogic<IngredientLocal, IngredientRemote>

    private val ingredientLocalList = LinkedList<IngredientLocal>().apply {
        add(IngredientLocal(1, 2, 3, null, null, "Ingredient Local 1"))
    }

    private val ingredientRemoteList = LinkedList<IngredientRemote>().apply {
        add(
            IngredientRemote.fromLocal(
            IngredientLocal(1, 2, 3, null, null, "Ingredient Local 1"), 4)
        )
    }

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

    @Before
    fun setup() {
        Mockito.`when`(
            mockedIngredientSyncLogic.compare(
                ArgumentMatchers.any(),
                ArgumentMatchers.any()
            )
        )
            .thenReturn(SyncLogic.SyncAction.DO_NOTHING)
    }

    /*
    When setting just one list, no sync is triggered.
    When setting both lists and the lists are not empty, the sync is triggered
     */
    @Test
    @Suppress("Unchecked_Cast")
    fun setLists() {
        val syncHelper =
            SyncHelper(syncManager as SyncManager<ModelLocal, Model>) {}

        syncHelper.setLocalList(ingredientLocalList as LinkedList<ModelLocal>)
        Mockito.verify(mockedIngredientSyncLogic, never())
            ?.compare(ArgumentMatchers.any(), ArgumentMatchers.any())

        syncHelper.setRemoteList(ingredientRemoteList as LinkedList<Model>)
        Mockito.verify(mockedIngredientSyncLogic)
            ?.compare(ArgumentMatchers.any(), ArgumentMatchers.any())
    }
}