package com.pingwinek.jens.cookandbake.sources

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.pingwinek.jens.cookandbake.lib.firestore.FirestoreDataAccessManager
import com.pingwinek.jens.cookandbake.models.IngredientFB
import com.pingwinek.jens.cookandbake.utils.SingletonHolder
import java.util.LinkedList

class IngredientSourceFB private constructor(private val firestore: FirebaseFirestore)
    : IngredientSource<IngredientFB> {

    private val auth: FirebaseAuth = Firebase.auth
    private val basePath: String = "/user"

    //TODO Check if needed or remove from interface
    override suspend fun getAll() : LinkedList<IngredientFB> {
        var list = LinkedList<IngredientFB>()
        /*
        if (auth.currentUser != null) {
            list = getAll(buildIngredientsCollRef(auth.uid!!))
        } else {

        }

         */
        return list
    }

    override suspend fun getAllForRecipeId(recipeId: String) : LinkedList<IngredientFB> {
        var list = LinkedList<IngredientFB>()
        if (auth.currentUser != null && auth.currentUser!!.isEmailVerified) {
            list = getAll(buildIngredientsCollRef(auth.uid!!, recipeId), recipeId)
        } else {
            Log.i(this::class.java.name, "unauthorized getAll")
        }
        return list
    }

    //TODO Check if needed or remove from interface
    override suspend fun get(id: String) : IngredientFB {
        var ingredientFB: IngredientFB = IngredientFB("", null, null, null, "")
        if (auth.currentUser != null && auth.currentUser!!.isEmailVerified) {

        } else {
            Log.i(this::class.java.name, "unauthorized get")
        }
        return ingredientFB
    }

    override suspend fun new(item: IngredientFB) : IngredientFB {
        var ingredientFB = item
        if (auth.currentUser != null && auth.currentUser!!.isEmailVerified) {
            ingredientFB = new(buildIngredientsCollRef(auth.uid!!, item.recipeId), item)
        } else {
            Log.i(this::class.java.name, "unauthorized new")
        }
        return ingredientFB
    }

    override suspend fun update(item: IngredientFB) : IngredientFB {
        var ingredientFB = item
        if (auth.currentUser != null && auth.currentUser!!.isEmailVerified) {
            ingredientFB = update(buildIngredientDocRef(auth.uid!!, item.recipeId, item.id), item)
        } else {
            Log.i(this::class.java.name, "unauthorized update")
        }
        return ingredientFB
    }

    override suspend fun delete(item: IngredientFB) : Boolean {
        return if (auth.currentUser != null && auth.currentUser!!.isEmailVerified) {
            delete(buildIngredientDocRef(auth.uid!!, item.recipeId, item.id))
        } else {
            Log.i(this::class.java.name, "unauthorized delete")
            false
        }
    }

    private suspend fun getAll(collRef: CollectionReference, recipeID: String) : LinkedList<IngredientFB> {
        return FirestoreDataAccessManager.getAll(collRef) {
            IngredientFB(it, recipeID)
        }
    }

    private suspend fun get(documentReference: DocumentReference, recipeID: String) : IngredientFB {
        return FirestoreDataAccessManager.get(documentReference) {
            IngredientFB(it, recipeID)
        }
    }

    private suspend fun new(collRef: CollectionReference, ingredientFB: IngredientFB) : IngredientFB {
        return FirestoreDataAccessManager.new(collRef, ingredientFB.documentData) {
            IngredientFB(it, ingredientFB.recipeId)
        }
    }

    private suspend fun update(docRef: DocumentReference, ingredientFB: IngredientFB) : IngredientFB {
        return FirestoreDataAccessManager.update(docRef, ingredientFB.documentData) {
            IngredientFB(it, ingredientFB.recipeId)
        }
    }

    private suspend fun delete(docRef: DocumentReference) : Boolean {
        return FirestoreDataAccessManager.delete(docRef) {
            true
        }
    }

    //TODO handle exceptions
    private fun buildRecipesCollRef(userID: String) : CollectionReference {
        if (userID.isEmpty()) throw IllegalArgumentException("userID size 0 not allowed")
        return firestore.collection(basePath).document(userID).collection("recipe")
    }

    private fun buildIngredientsCollRef(userID: String, recipeID: String) : CollectionReference {
        if (recipeID.isEmpty()) throw IllegalArgumentException("recipeID size 0 not allowed")
        return buildRecipesCollRef(userID).document(recipeID).collection("ingredient")
    }

    private fun buildIngredientDocRef(userID: String, recipeID: String, ingredientID: String) : DocumentReference {
        if (ingredientID.isEmpty()) throw IllegalArgumentException("ingredientID size 0 not allowed")
        return buildIngredientsCollRef(userID, recipeID).document(ingredientID)
    }

    companion object : SingletonHolder<IngredientSourceFB, FirebaseFirestore>(::IngredientSourceFB)

}