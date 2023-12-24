package com.pingwinek.jens.cookandbake.sources

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.pingwinek.jens.cookandbake.lib.firestore.FirestoreDataAccessManager
import com.pingwinek.jens.cookandbake.models.RecipeFB
import com.pingwinek.jens.cookandbake.utils.SingletonHolder
import java.util.LinkedList

/**
 * Source to retrieve and manipulate recipes from Firebase
 *
 * @property firestore
 */
class RecipeSourceFB private constructor(private val firestore: FirebaseFirestore) :
    RecipeSource<RecipeFB> {

    private val auth: FirebaseAuth = Firebase.auth
    private val basePath: String = "/user"

    override suspend fun getAll(): LinkedList<RecipeFB> {
        var list = LinkedList<RecipeFB>()
        if (auth.currentUser != null) {
            list = getAll(buildRecipesCollRef(auth.uid!!))
        } else {
            Log.w(this::class.java.name, "unauthorized getAll")
        }
        return list
    }

    // does not work for firestore, because id is not Int
    override suspend fun get(id: String) : RecipeFB {
        var recipeFB: RecipeFB = RecipeFB("","","","",0)
        if (auth.currentUser != null) {
            recipeFB = get(buildRecipeDocRef(auth.uid!!, id))
        } else {
            Log.w(this::class.java.name, "unauthorized get")
        }
        return recipeFB
    }

    /**
     *
     */
    override suspend fun new(item: RecipeFB) : RecipeFB {
        var recipeFB: RecipeFB = item
        if (auth.currentUser != null) {
            recipeFB = add(buildRecipesCollRef(auth.uid!!), item)
        } else {
            Log.w(this::class.java.name, "unauthorized new")
        }
        return recipeFB
    }

    /**
     * Returns null if for any reason there is no corresponding item to be updated
     */
    override suspend fun update(item: RecipeFB) : RecipeFB {
        var recipeFB: RecipeFB = item
        if (auth.currentUser != null) {
            recipeFB = update(buildRecipeDocRef(auth.uid!!, item.id), item)
        } else {
            Log.w(this::class.java.name, "unauthorized update")
        }
        return recipeFB
    }

    override suspend fun delete(item: RecipeFB) : Boolean {
        auth.currentUser?.run { return delete(buildRecipeDocRef(auth.uid!!, item.id)) } ?: return false
    }

    private fun buildRecipesCollRef(userID: String) : CollectionReference {
        return firestore.collection(basePath).document(userID).collection("recipe")
    }

    private fun buildRecipeDocRef(userID: String, recipeID: String) : DocumentReference {
        return buildRecipesCollRef(userID).document(recipeID)
    }

    private suspend fun get(docRef: DocumentReference) : RecipeFB {
        return FirestoreDataAccessManager.get(docRef) {
            RecipeFB(it)
        }
    }

    private suspend fun getAll(collRef: CollectionReference) : LinkedList<RecipeFB> {
        return FirestoreDataAccessManager.getAll(collRef) {
            RecipeFB(it)
        }
    }

    private suspend fun add(collRef: CollectionReference, recipeFB: RecipeFB) : RecipeFB {
        return FirestoreDataAccessManager.new(collRef, recipeFB) {
            RecipeFB(it)
        }
    }

    private suspend fun update(docRef: DocumentReference, recipeFB: RecipeFB) : RecipeFB {
        return FirestoreDataAccessManager.update(docRef, recipeFB) {
            RecipeFB(it)
        }
    }

    private suspend fun delete(docRef: DocumentReference) : Boolean {
        return FirestoreDataAccessManager.delete<RecipeFB>(docRef) {
            true
        }
    }

    companion object : SingletonHolder<RecipeSourceFB, FirebaseFirestore>(::RecipeSourceFB)
}