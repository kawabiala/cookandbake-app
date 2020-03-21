package com.pingwinek.jens.cookandbake

import android.app.Application
import com.pingwinek.jens.cookandbake.lib.sync.Source
import com.pingwinek.jens.cookandbake.models.IngredientLocal
import com.pingwinek.jens.cookandbake.models.IngredientRemote
import com.pingwinek.jens.cookandbake.models.RecipeLocal
import com.pingwinek.jens.cookandbake.models.RecipeRemote
import com.pingwinek.jens.cookandbake.sources.*
import com.pingwinek.jens.cookandbake.sync.SyncManager
import org.junit.Test
import org.mockito.AdditionalAnswers.delegatesTo
import org.mockito.Mockito.*
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.companionObject
import kotlin.reflect.full.companionObjectInstance
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.superclasses
import kotlin.reflect.jvm.isAccessible

class SyncManagerTest {

    // Testdata-Lists

    private val ingredientsLocal = LinkedList<IngredientLocal>()
    private val ingredientsRemote = LinkedList<IngredientRemote>()
    private val recipesLocal = LinkedList<RecipeLocal>()
    private val recipesRemote = LinkedList<RecipeRemote>()

    //Mocks

    /*
    1.  Mock the sources, i.e. RecipeSourceLocal, RecipeSourceRemote, IngredientSourceLocal, IngredientSourceRemote
        a)  All of them are Singletons, thus we replace the value for instance property in the companion object.
        b)  Mockito can't handle the callback parameters: Mockito needs a class, and Kotlin lambdas are no class;
            thus, rather than just mocking, we delegate to an anonymous mock object
        c)  Now, Mockito doesn't see the method calls anymore; therefor we introduce a mocked "Verifier"
            and verify call of the Verifiers methods
    */
    init {
        ///////// Recipe Source Local /////////
        mockSingletonHolderInstance(
            RecipeSourceLocal::class,
            mock(
                RecipeSourceLocal::class.java, delegatesTo<RecipeSourceLocal>(
                    object : RecipeSource<RecipeLocal> {
                        override fun getAll(callback: (Source.Status, LinkedList<RecipeLocal>) -> Unit) {
                            callback(Source.Status.SUCCESS, recipesLocal)
                        }

                        override fun get(id: Int, callback: (Source.Status, RecipeLocal?) -> Unit) {
                            callback(Source.Status.SUCCESS, recipesLocal.find { recipeLocal ->
                                recipeLocal.id == id
                            })
                        }

                        fun getForRemoteId(remoteId: Int, callback: (Source.Status, RecipeLocal?) -> Unit) {
                            callback(Source.Status.SUCCESS, recipesLocal.find { recipeLocal ->
                                recipeLocal.remoteId == remoteId
                            })
                        }

                        override fun update(
                            item: RecipeLocal,
                            callback: (Source.Status, RecipeLocal?) -> Unit
                        ) {
                            mockedVerifier.callMe("Update locally ${item.title}, id: ${item.id}, remoteId: ${item.remoteId}")
                            callback(Source.Status.SUCCESS, item)
                        }

                        override fun new(item: RecipeLocal, callback: (Source.Status, RecipeLocal?) -> Unit) {
                            val newRecipeLocal = RecipeLocal(
                                10,
                                item.remoteId,
                                item.title,
                                item.description,
                                item.instruction
                            )
                            mockedVerifier.callMe("Insert locally ${newRecipeLocal.title}, id: ${newRecipeLocal.id}, remoteId: ${newRecipeLocal.remoteId}")
                            callback(Source.Status.SUCCESS, newRecipeLocal)
                        }

                        override fun delete(id: Int, callback: (Source.Status) -> Unit) {
                            mockedVerifier.callMe("Delete locally ${recipesLocal.find { recipeLocal ->
                                recipeLocal.id == id
                            }?.title.toString()}")
                            callback(Source.Status.SUCCESS)
                        }

                        fun toLocalId(remoteId: Int, callback: (Int?) -> Unit) {
                            getForRemoteId(remoteId) { status, recipeLocal ->
                                if (status == Source.Status.SUCCESS && recipeLocal != null) {
                                    callback(recipeLocal.id)
                                } else {
                                    callback(null)
                                }
                            }
                        }

                        fun toRemoteId(localId: Int, callback: (Int?) -> Unit) {
                            get(localId) { status, recipeLocal ->
                                if (status == Source.Status.SUCCESS && recipeLocal != null) {
                                    callback(recipeLocal.remoteId)
                                } else {
                                    callback(null)
                                }
                            }
                        }
                    }
                )
            )
        )

        ///////// Recipe Source Remote /////////
        mockSingletonHolderInstance(
            RecipeSourceRemote::class,
            mock(
                RecipeSourceRemote::class.java, delegatesTo<RecipeSourceRemote>(
                    object : RecipeSource<RecipeRemote> {
                        override fun getAll(callback: (Source.Status, LinkedList<RecipeRemote>) -> Unit) {
                            callback(Source.Status.SUCCESS, recipesRemote)
                        }

                        override fun get(id: Int, callback: (Source.Status, RecipeRemote?) -> Unit) {
                            callback(Source.Status.SUCCESS, recipesRemote.find { recipeRemote ->
                                recipeRemote.id == id
                            })
                        }

                        override fun update(
                            item: RecipeRemote,
                            callback: (Source.Status, RecipeRemote?) -> Unit
                        ) {
                            mockedVerifier.callMe("Update remotely ${item.title}")
                            callback(Source.Status.SUCCESS, item)
                        }

                        override fun new(
                            item: RecipeRemote,
                            callback: (Source.Status, RecipeRemote?) -> Unit
                        ) {
                            val newRecipeRemote = createRecipeRemote(10, item.title)
                            mockedVerifier.callMe("Insert remotely ${newRecipeRemote.title}, id: ${newRecipeRemote.id}")
                            callback(Source.Status.SUCCESS, newRecipeRemote)
                        }

                        override fun delete(id: Int, callback: (Source.Status) -> Unit) {
                            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                        }
                    }
                )
            )
        )

        ///////// Ingredient Source Local /////////
        mockSingletonHolderInstance(
            IngredientSourceLocal::class,
            mock(
                IngredientSourceLocal::class.java, delegatesTo<IngredientSourceLocal>(
                    object : IngredientSource<IngredientLocal> {
                        override fun getAll(callback: (Source.Status, LinkedList<IngredientLocal>) -> Unit) {
                            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                        }

                        override fun getAllForRecipeId(recipeId: Int, callback: (Source.Status, LinkedList<IngredientLocal>) -> Unit) {
                            callback(Source.Status.SUCCESS, LinkedList(ingredientsLocal.filter { ingredientLocal ->
                                ingredientLocal.recipeId == recipeId
                            }))
                        }

                        override fun get(id: Int, callback: (Source.Status, IngredientLocal?) -> Unit) {
                            callback(Source.Status.SUCCESS, ingredientsLocal.find { ingredientLocal ->
                                ingredientLocal.id == id
                            })
                        }

                        override fun update(
                            item: IngredientLocal,
                            callback: (Source.Status, IngredientLocal?) -> Unit
                        ) {
                            mockedVerifier.callMe("Update locally ${item.name}, id: ${item.id}, remoteId: ${item.remoteId}, recipeId: ${item.recipeId}")
                            callback(Source.Status.SUCCESS, item)
                        }

                        override fun new(
                            item: IngredientLocal,
                            callback: (Source.Status, IngredientLocal?) -> Unit
                        ) {
                            val newIngredientLocal = IngredientLocal(
                                10,
                                item.remoteId,
                                item.recipeId,
                                item.quantity,
                                item.unity,
                                item.name
                            )
                            mockedVerifier.callMe("Insert locally ${newIngredientLocal.name}, id: ${newIngredientLocal.id}, remoteId: ${newIngredientLocal.remoteId}, recipeId: ${newIngredientLocal.recipeId}")
                            callback(Source.Status.SUCCESS, newIngredientLocal)
                        }

                        override fun delete(id: Int, callback: (Source.Status) -> Unit) {
                            mockedVerifier.callMe("Delete locally ${ingredientsLocal.find { ingredientLocal ->
                                ingredientLocal.id == id
                            }?.name.toString()}")
                            callback(Source.Status.SUCCESS)
                        }
                    }
                )
            )
        )

        ///////// Ingredient Source Remote /////////
        mockSingletonHolderInstance(
            IngredientSourceRemote::class,
            mock(
                IngredientSourceRemote::class.java, delegatesTo<IngredientSourceRemote>(
                    object : IngredientSource<IngredientRemote> {
                        override fun getAll(callback: (Source.Status, LinkedList<IngredientRemote>) -> Unit) {
                            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                        }

                        override fun getAllForRecipeId(recipeId: Int, callback: (Source.Status, LinkedList<IngredientRemote>) -> Unit) {
                            callback(Source.Status.SUCCESS, LinkedList(ingredientsRemote.filter { ingredientRemote ->
                                ingredientRemote.recipeId == recipeId
                            }))
                        }

                        override fun get(id: Int, callback: (Source.Status, IngredientRemote?) -> Unit) {
                            callback(Source.Status.SUCCESS, ingredientsRemote.find { ingredientRemote ->
                                ingredientRemote.id == id
                            })
                        }

                        override fun update(
                            item: IngredientRemote,
                            callback: (Source.Status, IngredientRemote?) -> Unit
                        ) {
                            mockedVerifier.callMe("Update remotely ${item.name}")
                            callback(Source.Status.SUCCESS, item)
                        }

                        override fun new(
                            item: IngredientRemote,
                            callback: (Source.Status, IngredientRemote?) -> Unit
                        ) {
                            val newIngredientRemote = createIngredientRemote(10, item.recipeId, item.name)
                            mockedVerifier.callMe("Insert remotely ${newIngredientRemote.name}, id: ${newIngredientRemote.id}, recipeId: ${newIngredientRemote.recipeId}")
                            callback(Source.Status.SUCCESS, newIngredientRemote)
                        }

                        override fun delete(id: Int, callback: (Source.Status) -> Unit) {
                            mockedVerifier.callMe("Delete remotely Ingredient Local $id")
                            callback(Source.Status.SUCCESS)
                        }
                    }
                )
            )
        )
    }

    /*
    2.  mock the Application class just for calling the getInstance of SyncManager
     */
    private var application = mock(Application::class.java)

    /*
    3.  IMPORTANT: The first call of SyncManager.getInstance must happen only after creating the mocks of the sources;
        otherwise the respective properties of SyncManager will point to the unmocked objects
     */
    private val syncManager = SyncManager.getInstance(application)

    /*
    4.  the mocked Verifier will be instantiated per test section (purpose of mocked Verifier see 1.)
     */
    private lateinit var mockedVerifier: Verifier


    /////////// Testing /////////////

    /*
    We put all tests into 1 test method, because otherwise the test class will be reinitialized each time
    resulting in wrong references in Singletons
     */
    @Test
    fun test() {
        syncRecipes_test()
        syncRecipe_test()
        syncIngredients_test()
        syncIngredient_test()
    }

    private fun syncRecipe_test() {

        mockedVerifier = mock(Verifier::class.java)

        ////// Tests //////

        // CASE 1: no local recipe available
        clearTestData()
        syncManager.syncRecipe(1) { mockedVerifier.callOnDone() }

        // CASE 2: no local recipe available, and remote not reachable
        clearTestData()
        recipesRemote.add(createRecipeRemote(1, "Recipe Remote 1"))
        syncManager.syncRecipe(1) { mockedVerifier.callOnDone() }

        // CASE 3: local recipe with remoteId null -> insert remotely
        clearTestData()
        recipesLocal.add(RecipeLocal(1, null, "Recipe Local 1", null, null))
        syncManager.syncRecipe(1) { mockedVerifier.callOnDone() }

        // CASE 4: local recipe with remoteId not null -> delete locally
        clearTestData()
        recipesLocal.add(RecipeLocal(1, 1, "Recipe Local 1", null, null))
        syncManager.syncRecipe(1) { mockedVerifier.callOnDone() }

        // CASE 6: Remote recipe more recent than local
        clearTestData()
        recipesLocal.add(RecipeLocal(1, 1, "Recipe Local 1", null, null))
        Thread.sleep(1) // make sure, remote is more recent than local
        recipesRemote.add(createRecipeRemote(1, "Recipe Remote 1"))
        syncManager.syncRecipe(1) { mockedVerifier.callOnDone() }

        // CASE 7: Local recipe more recent than remote
        clearTestData()
        recipesRemote.add(createRecipeRemote(1, "Recipe Remote 1"))
        Thread.sleep(1) // make sure, local is more recent than remote
        recipesLocal.add(RecipeLocal(1, 1, "Recipe Local 1", null, null))
        syncManager.syncRecipe(1) { mockedVerifier.callOnDone() }


        ////// Verification //////

        val orderedMock = inOrder(mockedVerifier)

        // Case 1 and 2: nothing synced
        orderedMock.verify(mockedVerifier, times(2)).callOnDone()

        // CASE 3: local inserted into remote
        orderedMock.verify(mockedVerifier).callMe("Insert remotely Recipe Local 1, id: 10")
        orderedMock.verify(mockedVerifier)
            .callMe("Update locally Recipe Local 1, id: 1, remoteId: 10")
        orderedMock.verify(mockedVerifier).callOnDone()

        // CASE 4: local deleted locally
        orderedMock.verify(mockedVerifier).callMe("Delete locally Recipe Local 1")
        orderedMock.verify(mockedVerifier).callOnDone()

        // CASE 6: update locally
        orderedMock.verify(mockedVerifier)
            .callMe("Update locally Recipe Remote 1, id: 1, remoteId: 1")
        orderedMock.verify(mockedVerifier).callOnDone()

        // CASE 7: update remotely
        orderedMock.verify(mockedVerifier).callMe("Update remotely Recipe Local 1")
        orderedMock.verify(mockedVerifier).callOnDone()
    }

    private fun syncRecipes_test() {

        mockedVerifier = mock(Verifier::class.java)

        ////// Tests //////

        // CASE 1: empty lists
        clearTestData()
        syncManager.syncRecipes { mockedVerifier.callOnDone() }

        // CASE 2: no local recipe available
        clearTestData()
        recipesRemote.add(createRecipeRemote(1, "Recipe Remote 1"))
        syncManager.syncRecipes { mockedVerifier.callOnDone() }

        // CASE 3: local recipe with remoteId null -> insert remotely
        clearTestData()
        recipesLocal.add(RecipeLocal(1, null, "Recipe Local 1", null, null))
        syncManager.syncRecipes { mockedVerifier.callOnDone() }

        // CASE 4: local recipe with remoteId not null -> delete locally
        clearTestData()
        recipesLocal.add(RecipeLocal(1, 1, "Recipe Local 1", null, null))
        syncManager.syncRecipes { mockedVerifier.callOnDone() }

        // CASE 6: Remote recipe more recent than local
        clearTestData()
        recipesLocal.add(RecipeLocal(1, 1, "Recipe Local 1", null, null))
        Thread.sleep(1) // make sure, remote is more recent than local
        recipesRemote.add(createRecipeRemote(1, "Recipe Remote 1"))
        syncManager.syncRecipes { mockedVerifier.callOnDone() }

        // CASE 7: Local recipe more recent than remote
        clearTestData()
        recipesRemote.add(createRecipeRemote(1, "Recipe Remote 1"))
        Thread.sleep(1) // make sure, local is more recent than remote
        recipesLocal.add(RecipeLocal(1, 1, "Recipe Local 1", null, null))
        syncManager.syncRecipes { mockedVerifier.callOnDone() }


        ////// Verification //////

        val orderedMock = inOrder(mockedVerifier)

        // CASE 1: do nothing
        orderedMock.verify(mockedVerifier).callOnDone()

        // CASE 2: remote inserted into local
        orderedMock.verify(mockedVerifier)
            .callMe("Insert locally Recipe Remote 1, id: 10, remoteId: 1")
        orderedMock.verify(mockedVerifier).callOnDone()

        // CASE 3: local inserted into remote
        orderedMock.verify(mockedVerifier).callMe("Insert remotely Recipe Local 1, id: 10")
        orderedMock.verify(mockedVerifier)
            .callMe("Update locally Recipe Local 1, id: 1, remoteId: 10")
        orderedMock.verify(mockedVerifier).callOnDone()

        // CASE 4: local deleted locally
        orderedMock.verify(mockedVerifier).callMe("Delete locally Recipe Local 1")
        orderedMock.verify(mockedVerifier).callOnDone()

        // CASE 6: update locally
        orderedMock.verify(mockedVerifier).callMe("Update locally Recipe Remote 1, id: 1, remoteId: 1")
        orderedMock.verify(mockedVerifier).callOnDone()

        // CASE 7: update remotely
        orderedMock.verify(mockedVerifier).callMe("Update remotely Recipe Local 1")
        orderedMock.verify(mockedVerifier).callOnDone()
    }

    private fun syncIngredient_test() {

        mockedVerifier = mock(Verifier::class.java)

        ////// Tests //////

        // Case 1: no local ingredient available
        clearTestData()
        syncManager.syncIngredient(1) { mockedVerifier.callOnDone() }

        // Case 2: same as 1, though remote would be existent (but not reachable from syncIngredient)
        clearTestData()
        ingredientsRemote.add(createIngredientRemote(2, 1, "Ingredient Remote 2"))
        syncManager.syncIngredient(2) { mockedVerifier.callOnDone() }

        // Case 3: Local ingredient with remoteId isNull -> insert remotely
        clearTestData()
        ingredientsLocal.add(IngredientLocal(3, null, 3, null, null, "Ingredient Local 3"))
        recipesLocal.add(RecipeLocal(3, 3, "Recipe Local 3", null, null))
        syncManager.syncIngredient(3) { mockedVerifier.callOnDone() }

        // Case 4: Local ingredient with remoteId, but no remote ingredient existent -> delete locally
        clearTestData()
        ingredientsLocal.add(IngredientLocal(4, 4, 1, null, null, "Ingredient Local 4"))
        syncManager.syncIngredient(4) { mockedVerifier.callOnDone() }

        // Case 6: Remote ingredient more recent than local -> update locally
        clearTestData()
        ingredientsLocal.add(IngredientLocal(6, 6, 1, null, null, "Ingredient Local 6"))
        Thread.sleep(1) // make sure, remote is more recent than local
        ingredientsRemote.add(createIngredientRemote(6, 1,"Ingredient Remote 6"))
        syncManager.syncIngredient(6) { mockedVerifier.callOnDone() }

        // Case 7: Local has flag deleted and is more recent than remote -> delete remotely
        clearTestData()
        ingredientsRemote.add(createIngredientRemote(7, 1, "Ingredient Remote 7"))
        ingredientsLocal.add(IngredientLocal(7, 7, 1, null, null, "Ingredient Local 7", Date().time, true))
        syncManager.syncIngredient(7) { mockedVerifier.callOnDone() }

        // Case 8: Local ingredient more recent than remote -> update remotely
        clearTestData()
        ingredientsRemote.add(createIngredientRemote(8, 1,"Ingredient Remote 8"))
        Thread.sleep(1) // make sure, that local is more recent than local
        ingredientsLocal.add(IngredientLocal(8, 8, 1, null, null, "Ingredient Local 8"))
        syncManager.syncIngredient(8) { mockedVerifier.callOnDone() }


        ////// Verification //////

        val orderedMock = inOrder(mockedVerifier)

        // Case 1 and 2: nothing synced
        orderedMock.verify(mockedVerifier, times(2)).callOnDone()

        // Case 3: local inserted into remote
        orderedMock.verify(mockedVerifier)
            .callMe("Insert remotely Ingredient Local 3, id: 10, recipeId: 3")
        orderedMock.verify(mockedVerifier)
            .callMe("Update locally Ingredient Local 3, id: 3, remoteId: 10, recipeId: 3")
        orderedMock.verify(mockedVerifier).callOnDone()

        // Case 4: local deleted locally
        orderedMock.verify(mockedVerifier).callMe("Delete locally Ingredient Local 4")
        orderedMock.verify(mockedVerifier).callOnDone()

        // Case 6: local synced to remote
        orderedMock.verify(mockedVerifier)
            .callMe("Update locally Ingredient Remote 6, id: 6, remoteId: 6, recipeId: 1")
        orderedMock.verify(mockedVerifier).callOnDone()

        // Case 7: delete remotely
        orderedMock.verify(mockedVerifier).callMe("Delete remotely Ingredient Local 7")
        orderedMock.verify(mockedVerifier).callOnDone()

        // Case 8: remote synced to local
        orderedMock.verify(mockedVerifier).callMe("Update remotely Ingredient Local 8")
        orderedMock.verify(mockedVerifier).callOnDone()

    }

    fun syncIngredients_test() {

        mockedVerifier = mock(Verifier::class.java)

        ////// Tests //////

        // CASE 1: no local ingredient available
        clearTestData()
        recipesLocal.add(RecipeLocal(1,2, "Recipe Local", null, null))
        syncManager.syncIngredients(1) { mockedVerifier.callOnDone() }

        // CASE 2: only remote ingredient
        clearTestData()
        ingredientsRemote.add(createIngredientRemote(2, 2,"Ingredient Remote 2"))
        recipesRemote.add(createRecipeRemote(2, "Recipe Remote"))
        recipesLocal.add(RecipeLocal(1,2, "Recipe Local", null, null))
        syncManager.syncIngredients(1) { mockedVerifier.callOnDone() }


        ////// Verification //////

        val orderedMock = inOrder(mockedVerifier)

        // CASE 1: nothing synced
        orderedMock.verify(mockedVerifier).callOnDone()

        // CASE 2: insert locally
        orderedMock.verify(mockedVerifier).callMe("Insert locally Ingredient Remote 2, id: 10, remoteId: 2, recipeId: 1")
        orderedMock.verify(mockedVerifier).callOnDone()
    }


    //////////////////// Other stuff ////////////////////

    private fun clearTestData() {
        ingredientsLocal.clear()
        ingredientsRemote.clear()
        recipesLocal.clear()
        recipesRemote.clear()
    }

    private fun createRecipeRemote(id: Int, title: String) : RecipeRemote {
        return RecipeRemote.fromLocal(
            RecipeLocal(0, id, title, null, null)
        )
    }

    private fun createIngredientRemote(id: Int, recipeId: Int, name: String) : IngredientRemote {
        return IngredientRemote.fromLocal(
            IngredientLocal(0, id, 0, null, null, name),
            recipeId
        )
    }

    private fun <T : Any> mockSingletonHolderInstance(singleton: KClass<T>, mock: T) {
        val clsSingletonHolder = singleton.companionObject?.superclasses?.find {
            it.qualifiedName == SingletonHolder::class.qualifiedName
        } ?: return

        val instanceField = clsSingletonHolder.memberProperties.find {
            it.name == "instance"
        } ?: return

        instanceField.isAccessible = true
        if (instanceField.getter.call(singleton.companionObjectInstance) == null) {
            if (instanceField is KMutableProperty<*>) {
                instanceField.setter.call(singleton.companionObjectInstance, mock)
            }
        }
    }

    class Verifier {
        fun callMe(calledBy: String) {}
        fun callOnDone() {}
    }
}