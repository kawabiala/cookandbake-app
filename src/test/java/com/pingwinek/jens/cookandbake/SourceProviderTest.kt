package com.pingwinek.jens.cookandbake

import android.app.Application
import com.pingwinek.jens.cookandbake.models.IngredientLocal
import com.pingwinek.jens.cookandbake.models.IngredientRemote
import com.pingwinek.jens.cookandbake.sources.IngredientSourceLocal
import com.pingwinek.jens.cookandbake.sources.IngredientSourceRemote
import com.pingwinek.jens.cookandbake.lib.sync.SourceProvider
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock

class SourceProviderTest {

    val application = mock(Application::class.java)
    val ingredientSourceLocal = IngredientSourceLocal.getInstance(application)
    val ingredientSourceRemote = IngredientSourceRemote.getInstance(application)

    @Before
    fun setUp() {
    }

    @Test
    fun getLocalSources() {
        SourceProvider.registerLocalSource(ingredientSourceLocal)
        assert(SourceProvider.getLocalSource<IngredientLocal>() == ingredientSourceLocal)
    }

    @Test
    fun getRemoteSources() {
        SourceProvider.registerRemoteSource(ingredientSourceRemote)
        assert(SourceProvider.getRemoteSource<IngredientRemote>() == ingredientSourceRemote)
    }

    // Todo: test remaining methods
}