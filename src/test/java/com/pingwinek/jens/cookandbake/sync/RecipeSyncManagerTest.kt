package com.pingwinek.jens.cookandbake.sync

import com.nhaarman.mockitokotlin2.*
import com.pingwinek.jens.cookandbake.lib.sync.Promise
import com.pingwinek.jens.cookandbake.models.RecipeLocal
import com.pingwinek.jens.cookandbake.models.RecipeRemote
import com.pingwinek.jens.cookandbake.sources.RecipeSourceLocal
import com.pingwinek.jens.cookandbake.sources.RecipeSourceRemote
import org.junit.Test
import java.util.*

class RecipeSyncManagerTest {
/*
    private val recipeLocal = mock<RecipeLocal>()
    private val recipeRemote = mock<RecipeRemote>()

    private val mockedRecipeSourceLocal = mock<RecipeSourceLocal>()
    private val mockedRecipeSourceRemote = mock<RecipeSourceRemote>()

    private val testRecipeSyncManager = RecipeSyncManager(mockedRecipeSourceLocal, mockedRecipeSourceRemote, RecipeSyncLogic())

    @Test
    fun getLocalParent() {
        testRecipeSyncManager.getLocalParent(1).setResultHandler {
            assert(it.status == Promise.Status.FAILURE)
            assert(it.value == null)
        }
    }

    @Test
    fun getLocalsByParent() {
        testRecipeSyncManager.getLocalsByParent(1).setResultHandler {
            assert(it.status == Promise.Status.FAILURE)
            assert(it.value == null)
        }
    }

    @Test
    fun getRemotesByParent() {
        testRecipeSyncManager.getRemotesByParent(1).setResultHandler {
            assert(it.status == Promise.Status.FAILURE)
            assert(it.value == null)
        }
    }

    @Test
    fun newLocal() {
        val lastModified = Date().time
        whenever(mockedRecipeSourceLocal.new(any())).thenReturn(Promise())
        whenever(recipeRemote.id).thenReturn(1)
        whenever(recipeRemote.title).thenReturn("Recipe Remote")
        whenever(recipeRemote.lastModified).thenReturn(lastModified)
        testRecipeSyncManager.newLocal(recipeRemote) {}
        verify(mockedRecipeSourceLocal).new(check {
            assert(it.id == 0)
            assert(it.title == "Recipe Remote")
            assert(it.lastModified == recipeRemote.lastModified)
        })
    }

    @Test
    fun newRemote() {
        val lastModified = Date().time

        whenever(recipeLocal.id).thenReturn(1)
        whenever(recipeLocal.remoteId).thenReturn(null)
        whenever(recipeLocal.title).thenReturn("Recipe Local")
        whenever(recipeLocal.lastModified).thenReturn(lastModified)

        whenever(recipeRemote.id).thenReturn(2)
        whenever(recipeRemote.title).thenReturn("Recipe Remote")
        whenever(recipeRemote.lastModified).thenReturn(lastModified)

        whenever(mockedRecipeSourceLocal.get(any())).thenReturn(Promise<RecipeLocal>().apply {
            setResult(Promise.Status.SUCCESS, recipeLocal)
        })
        whenever(mockedRecipeSourceRemote.new(any())).thenReturn(Promise<RecipeRemote>().apply {
            setResult(Promise.Status.SUCCESS, recipeRemote)
        })
        whenever(mockedRecipeSourceLocal.update(any())).thenReturn(Promise())
        testRecipeSyncManager.newRemote(recipeLocal) {}
        verify(mockedRecipeSourceLocal).get(any())
        verify(mockedRecipeSourceRemote).new(check {
            assert(it.id == 0)
            assert(it.title == "Recipe Local")
            assert(it.lastModified == lastModified)
        })
        verify(mockedRecipeSourceLocal).update(check {
            assert(it.id == 1)
            assert(it.remoteId == 2)
            assert(it.title == "Recipe Local")
            assert(it.lastModified > lastModified)
        })
    }

    @Test
    fun updateLocal() {
        whenever(recipeLocal.getUpdated(any<RecipeRemote>())).thenReturn(recipeLocal)
        whenever(mockedRecipeSourceLocal.update(any())).thenReturn(Promise())
        testRecipeSyncManager.updateLocal(recipeLocal, recipeRemote) {}
        verify(recipeLocal).getUpdated(recipeRemote)
        verify(mockedRecipeSourceLocal).update(recipeLocal)
    }

    @Test
    fun updateRemote() {
        whenever(recipeRemote.getUpdated(any<RecipeLocal>())).thenReturn(recipeRemote)
        whenever(mockedRecipeSourceRemote.update(any())).thenReturn(Promise())
        testRecipeSyncManager.updateRemote(recipeLocal, recipeRemote) {}
        verify(recipeRemote).getUpdated(recipeLocal)
        verify(mockedRecipeSourceRemote).update(recipeRemote)
    }

    @Test
    fun deleteLocal() {
        whenever(mockedRecipeSourceLocal.delete(any())).thenReturn(Promise())
        whenever(recipeLocal.id).thenReturn(1)
        testRecipeSyncManager.deleteLocal(recipeLocal) {}
        verify(mockedRecipeSourceLocal).delete(1)
    }

    @Test
    fun deleteRemote() {
        whenever(mockedRecipeSourceRemote.delete(any())).thenReturn(Promise())
        whenever(recipeRemote.id).thenReturn(1)
        testRecipeSyncManager.deleteRemote(recipeRemote) {}
        verify(mockedRecipeSourceRemote).delete(1)
    }

 */
}