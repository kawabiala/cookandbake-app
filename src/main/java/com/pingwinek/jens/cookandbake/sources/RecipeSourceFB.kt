package com.pingwinek.jens.cookandbake.sources

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.ktx.Firebase
import com.pingwinek.jens.cookandbake.lib.firestore.FirestoreDataAccessManager
import com.pingwinek.jens.cookandbake.models.RecipeFB
import com.pingwinek.jens.cookandbake.utils.SingletonHolder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.LinkedList
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Source to retrieve and manipulate recipes from Firebase
 *
 * @property firestore
 */
class RecipeSourceFB private constructor(private val firestore: FirebaseFirestore):
    RecipeSource<RecipeFB> {

    private val auth: FirebaseAuth = Firebase.auth
    private val basePath: String = "/user"

    override suspend fun getAll() : LinkedList<RecipeFB> {
        var list = LinkedList<RecipeFB>()
        if (auth.currentUser != null) {
            list = getAll(buildRecipesCollRef(auth.uid!!))
        } else {
            Log.w(this::class.java.name, "unauthorized getAll")
        }
        return list
    }

    // does not work for firestore, because id is not Int
    override suspend fun get(id: String) : RecipeFB? {
        var recipeFB: RecipeFB? = RecipeFB("","","","",0)
        if (auth.currentUser != null) {
            recipeFB = get(buildRecipeDocRef(auth.uid!!, id))
        } else {

        }
        return recipeFB
    }

    /**
     *
     */
    override suspend fun new(item: RecipeFB) : RecipeFB? {
        var recipeFB: RecipeFB? = item
        if (auth.currentUser != null) {
            recipeFB = add(buildRecipesCollRef(auth.uid!!), item)
        } else {

        }
        return recipeFB
    }

    /**
     * Returns null if for any reason there is no corresponding item to be updated
     */
    override suspend fun update(item: RecipeFB) : RecipeFB? {
        var recipeFB: RecipeFB? = item
        if (auth.currentUser != null) {
            recipeFB = update(buildRecipeDocRef(auth.uid!!, item.id), item)
        } else {

        }
        return recipeFB
    }

    override suspend fun delete(item: RecipeFB) : Boolean {
        auth.currentUser?.run { return delete(buildRecipeDocRef(auth.uid!!, item.id)) } ?: return false
    }

    /*
    suspend fun flagAsDeleted(id: Int) : RecipeLocal? {
        val toDelete = get(id)?.getDeleted() ?: return null
        return update(toDelete)
    }

     */

    private fun buildRecipesCollRef(userID: String) : CollectionReference {
        return firestore.collection(basePath).document(userID).collection("recipe")
    }

    private fun buildRecipeDocRef(userID: String, recipeID: String) : DocumentReference {
        return buildRecipesCollRef(userID).document(recipeID)
    }

    private suspend fun get(docRef: DocumentReference) : RecipeFB? {
        var documentSnapshot: DocumentSnapshot? = null

        withContext(Dispatchers.IO) {
            docRef.get().addOnSuccessListener {
                documentSnapshot = it
            }.addOnFailureListener { exception ->

            }
        }

        documentSnapshot?.let { return RecipeFB(it) } ?: return null
    }

    private suspend fun getAll(collRef: CollectionReference) : LinkedList<RecipeFB> {
        var list = LinkedList<RecipeFB>()
        Log.i(this::class.java.name, "before Firestore call")
        list = FirestoreDataAccessManager.getAll(collRef) {
            RecipeFB(it)
        }
        Log.i(this::class.java.name, "after Firestore call")
        return list
        var querySnapshot: QuerySnapshot? = null

        Log.i(this::class.java.name, "before withContext")
        withContext(Dispatchers.IO) {
            Log.i(this::class.java.name, "create Query and add Listener")
            collRef.get().addOnSuccessListener {
                Log.i(this::class.java.name, "success listener called")
                querySnapshot = it
            }.addOnFailureListener { exception ->
                logFailure(exception)
            }
        }
        Log.i(this::class.java.name, "after withContext")

        querySnapshot?.let { qs ->
            Log.i(this::class.java.name, "snapshot with size ${qs.size()}")
            qs.forEach {
                list.add(RecipeFB(it))
            }
        } ?: Log.i(this::class.java.name, "snapshot is null")
        return list
    }

    private suspend fun add(collRef: CollectionReference, recipeFB: RecipeFB) : RecipeFB {
        var docRef: DocumentReference? = null

        //withContext(Dispatchers.IO) {
            docRef = suspendCoroutine { continuation ->
                collRef.add(recipeFB.documentData).addOnSuccessListener(OnSuccessListener<DocumentReference> { docRef ->
                    continuation.resume(docRef)
                }).addOnFailureListener { exception ->
                    logFailure(exception)
                }
            }
        /* Alternatively with regular callback
            collRef.add(recipeFB.documentData).addOnSuccessListener {
                docRef = it
            }.addOnFailureListener { exception ->
                logFailure(exception)
            }

         */
        //}

        docRef?.let { return get(it) ?: recipeFB } ?: return recipeFB
    }

    private suspend fun update(docRef: DocumentReference, recipeFB: RecipeFB) : RecipeFB {
        //TODO FirestoreResultTaskHandler<Void, Unit>(docRef.set(recipeFB.documentData), {}, this.javaClass.name).getResult()
        withContext(Dispatchers.IO) {
            docRef.set(recipeFB.documentData).addOnSuccessListener {

            }.addOnFailureListener { exception ->
                logFailure(exception)
            }
        }

        return get(docRef) ?: recipeFB
    }

    private suspend fun delete(docRef: DocumentReference) : Boolean {
        var returnValue: Boolean = false

        //withContext(Dispatchers.IO) {
        //TODO returnValue = FirestoreResultTaskHandler<Void, Boolean>(docRef.delete(), {true}, this.javaClass.name).getResult()
            returnValue = suspendCoroutine { continuation ->
                docRef.delete().addOnSuccessListener(OnSuccessListener<Void> {
                    continuation.resume(true)
                }).addOnFailureListener { exception ->
                    logFailure(exception)
                }
            }
            /*
            docRef.delete().addOnSuccessListener {
                returnValue = true
            }.addOnFailureListener(OnFailureListener(this@RecipeSourceFB.javaClass.toString()))

             */
        //}

        return returnValue
    }

    private fun logFailure(exception: Exception) {
        Log.e(this.javaClass.name, exception.toString())
    }

    private class OnSuccessListener<T>(val onSuccessAction: (T) -> Unit): com.google.android.gms.tasks.OnSuccessListener<T> {
        override fun onSuccess(result: T) {
            onSuccessAction(result)
        }
    }

    private class OnFailureListener(val tag: String) : com.google.android.gms.tasks.OnFailureListener {
        override fun onFailure(exception: Exception) {
            Log.e(tag, exception.toString())
        }
    }

    private class FirestoreResultTaskHandler<In, out Out>(
        val task: Task<In>,
        val onSuccess: (In) -> Out,
        val logTag: String
    ) {
        suspend fun getResult() : Out {
            return suspendCoroutine { continuation ->
                task.addOnSuccessListener { t ->
                    continuation.resume(onSuccess(t))
                }.addOnFailureListener { exception ->
                    Log.e(logTag, exception.toString())
                }.addOnCanceledListener {
                    Log.w(logTag, "getResult cancelled")
                }
            }
        }
    }

    companion object : SingletonHolder<RecipeSourceFB, FirebaseFirestore>(::RecipeSourceFB)
}