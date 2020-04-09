package com.pingwinek.jens.cookandbake.sync

import com.nhaarman.mockitokotlin2.*
import com.pingwinek.jens.cookandbake.lib.sync.Promise
import com.pingwinek.jens.cookandbake.models.IngredientLocal
import com.pingwinek.jens.cookandbake.models.IngredientRemote
import com.pingwinek.jens.cookandbake.models.RecipeLocal
import com.pingwinek.jens.cookandbake.sources.IngredientSourceLocal
import com.pingwinek.jens.cookandbake.sources.IngredientSourceRemote
import com.pingwinek.jens.cookandbake.sources.RecipeSourceLocal
import org.junit.Test
import java.util.*

class IngredientSyncManagerTest {

    private val recipeLocal = mock<RecipeLocal>()
    private val ingredientLocal = mock<IngredientLocal>()
    private val ingredientRemote = mock<IngredientRemote>()

    private val mockedRecipeSourceLocal = mock<RecipeSourceLocal>()
    private val mockedIngredientSourceLocal = mock<IngredientSourceLocal>()
    private val mockedIngredientSourceRemote = mock<IngredientSourceRemote>()

    private val testIngredientSyncManager = IngredientSyncManager(
        mockedRecipeSourceLocal,
        mockedIngredientSourceLocal,
        mockedIngredientSourceRemote,
        IngredientSyncLogic()
    )

    @Test
    fun getLocalParent() {
        whenever(recipeLocal.id).thenReturn(1)
        whenever(mockedRecipeSourceLocal.get(any())).thenReturn(Promise<RecipeLocal>().apply {
            setResult(Promise.Status.SUCCESS, recipeLocal)
        })
        testIngredientSyncManager.getLocalParent(1).setResultHandler {
            assert(it.status == Promise.Status.SUCCESS)
            assert(it.value?.id == 1)
        }
        verify(mockedRecipeSourceLocal).get(1)
    }

    @Test
    fun getLocalsByParent() {
        whenever(mockedIngredientSourceLocal.getAllForRecipeId(any())).thenReturn(Promise<LinkedList<IngredientLocal>>().apply {
            setResult(Promise.Status.SUCCESS, LinkedList<IngredientLocal>().apply {
                add(ingredientLocal)
            })
        })
        testIngredientSyncManager.getLocalsByParent(1).setResultHandler {
            assert(it.status == Promise.Status.SUCCESS)
            assert(it.value?.get(0) == ingredientLocal)
        }
    }

    @Test
    fun getRemotesByParent() {
        whenever(mockedIngredientSourceRemote.getAllForRecipeId(any())).thenReturn(Promise<LinkedList<IngredientRemote>>().apply{
            setResult(Promise.Status.SUCCESS, LinkedList<IngredientRemote>().apply {
                add(ingredientRemote)
            })
        })
        testIngredientSyncManager.getRemotesByParent(1).setResultHandler {
            assert(it.status == Promise.Status.SUCCESS)
            assert(it.value?.get(0) == ingredientRemote)
        }
    }

    @Test
    fun newLocal() {
        val lastModified = Date().time
        whenever(ingredientRemote.id).thenReturn(1)
        whenever(ingredientRemote.recipeId).thenReturn(2)
        whenever(ingredientRemote.name).thenReturn("Recipe Remote")
        whenever(ingredientRemote.lastModified).thenReturn(lastModified)
        whenever(mockedRecipeSourceLocal.toLocalId(any())).thenReturn(Promise<Int>().apply {
            setResult(Promise.Status.SUCCESS, 3)
        })
        whenever(mockedIngredientSourceLocal.new(any())).thenReturn(Promise())
        testIngredientSyncManager.newLocal(ingredientRemote) {}
        verify(mockedRecipeSourceLocal).toLocalId(2)
        verify(mockedIngredientSourceLocal).new(check {
            assert(it.id == 0)
            assert(it.recipeId == 3)
            assert(it.name == "Recipe Remote")
            assert(it.lastModified == lastModified)
        })
    }

    @Test
    fun newRemote() {
        val lastModified = Date().time

        whenever(ingredientLocal.id).thenReturn(1)
        whenever(ingredientLocal.remoteId).thenReturn(null)
        whenever(ingredientLocal.recipeId).thenReturn(2)
        whenever(ingredientLocal.name).thenReturn("Recipe Local")
        whenever(ingredientLocal.lastModified).thenReturn(lastModified)

        whenever(ingredientRemote.id).thenReturn(4)

        whenever(mockedRecipeSourceLocal.toRemoteId(any())).thenReturn(Promise<Int>().apply {
            setResult(Promise.Status.SUCCESS, 3)
        })
        whenever(mockedIngredientSourceLocal.get(any())).thenReturn(Promise<IngredientLocal>().apply {
            setResult(Promise.Status.SUCCESS, ingredientLocal)
        })
        whenever(mockedIngredientSourceRemote.new(any())).thenReturn(Promise<IngredientRemote>().apply {
            setResult(Promise.Status.SUCCESS, ingredientRemote)
        })
        whenever(mockedIngredientSourceLocal.update(any())).thenReturn(Promise())
        testIngredientSyncManager.newRemote(ingredientLocal) {}
        verify(mockedRecipeSourceLocal).toRemoteId(2)
        verify(mockedIngredientSourceLocal).get(1)
        verify(mockedIngredientSourceRemote).new(check {
            assert(it.id == 0)
            assert(it.recipeId == 3)
            assert(it.name == "Recipe Local")
            assert(it.lastModified == lastModified)
        })
        verify(mockedIngredientSourceLocal).update(check {
            assert(it.id == 1)
            assert(it.remoteId == 4)
            assert(it.recipeId == 2)
            assert(it.name == "Recipe Local")
            assert(it.lastModified > lastModified)
        })

    }

    @Test
    fun updateLocal() {
        whenever(ingredientLocal.getUpdated(any<IngredientRemote>())).thenReturn(ingredientLocal)
        whenever(mockedIngredientSourceLocal.update(any())).thenReturn(Promise<IngredientLocal>().apply {
            setResult(Promise.Status.SUCCESS, ingredientLocal)
        })
        testIngredientSyncManager.updateLocal(ingredientLocal, ingredientRemote) {}
        verify(ingredientLocal).getUpdated(ingredientRemote)
        verify(mockedIngredientSourceLocal).update(ingredientLocal)
    }

    @Test
    fun updateRemote() {
        whenever(ingredientRemote.getUpdated(any())).thenReturn(ingredientRemote)
        whenever(mockedIngredientSourceRemote.update(any())).thenReturn(Promise())
        testIngredientSyncManager.updateRemote(ingredientLocal, ingredientRemote) {}
        verify(ingredientRemote).getUpdated(ingredientLocal)
        verify(mockedIngredientSourceRemote).update(ingredientRemote)
    }

    @Test
    fun deleteLocal() {
        whenever(ingredientLocal.id).thenReturn(1)
        whenever(mockedIngredientSourceLocal.delete(any())).thenReturn(Promise())
        testIngredientSyncManager.deleteLocal(ingredientLocal) {}
        verify(mockedIngredientSourceLocal).delete(1)
    }

    @Test
    fun deleteRemote() {
        whenever(ingredientRemote.id).thenReturn(1)
        whenever(mockedIngredientSourceRemote.delete(any())).thenReturn(Promise())
        testIngredientSyncManager.deleteRemote(ingredientRemote) {}
        verify(mockedIngredientSourceRemote).delete(1)
    }
}