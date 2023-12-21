package com.pingwinek.jens.cookandbake.sources

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
        if (auth.currentUser != null) {
            list = getAll(buildIngredientsCollRef(auth.uid!!, recipeId))
        } else {

        }
        return list
    }

    //TODO Check if needed or remove from interface
    override suspend fun get(id: String) : IngredientFB {
        var ingredientFB: IngredientFB = IngredientFB("", null, null, null, "")
        if (auth.currentUser != null) {

        } else {

        }
        return ingredientFB
    }

    override suspend fun new(item: IngredientFB) : IngredientFB {
        var ingredientFB = item
        if (auth.currentUser != null) {
            ingredientFB = new(buildIngredientsCollRef(auth.uid!!, item.recipeId), item)
        } else {

        }
        return ingredientFB
    }

    override suspend fun update(item: IngredientFB) : IngredientFB {
        var ingredientFB = item
        if (auth.currentUser != null) {
            ingredientFB = update(buildIngredientDocRef(auth.uid!!, item.recipeId, item.id), item)
        } else {

        }
        return ingredientFB
    }

    override suspend fun delete(item: IngredientFB) : Boolean {
        return if (auth.currentUser != null) {
            delete(buildIngredientDocRef(auth.uid!!, item.recipeId, item.id))
        } else {
            false
        }
    }

    private suspend fun getAll(collRef: CollectionReference) : LinkedList<IngredientFB> {
        return FirestoreDataAccessManager.getAll(collRef) {
            IngredientFB(it)
        }
    }

    private suspend fun get(documentReference: DocumentReference) : IngredientFB {
        return FirestoreDataAccessManager.get(documentReference) {
            IngredientFB(it)
        }
    }

    private suspend fun new(collRef: CollectionReference, item: IngredientFB) : IngredientFB {
        return FirestoreDataAccessManager.new(collRef, item) {
            IngredientFB(it)
        }
    }

    private suspend fun update(docRef: DocumentReference, item: IngredientFB) : IngredientFB {
        return FirestoreDataAccessManager.update(docRef, item) {
            IngredientFB(it)
        }
    }

    private suspend fun delete(docRef: DocumentReference) : Boolean {
        return FirestoreDataAccessManager.delete<IngredientFB>(docRef) {
            true
        }
    }

    private fun buildRecipesCollRef(userID: String) : CollectionReference {
        return firestore.collection(basePath).document(userID).collection("recipe")
    }

    private fun buildIngredientsCollRef(userID: String, recipeID: String) : CollectionReference {
        return buildRecipesCollRef(userID).document(recipeID).collection("ingredient")
    }

    private fun buildIngredientDocRef(userID: String, recipeID: String, ingredientID: String) : DocumentReference {
        return buildIngredientsCollRef(userID, recipeID).document(ingredientID)
    }

    companion object : SingletonHolder<IngredientSourceFB, FirebaseFirestore>(::IngredientSourceFB)

}