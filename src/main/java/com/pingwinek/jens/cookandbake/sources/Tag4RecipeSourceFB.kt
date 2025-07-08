package com.pingwinek.jens.cookandbake.sources

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.pingwinek.jens.cookandbake.lib.SingletonHolder
import com.pingwinek.jens.cookandbake.lib.firestore.FirestoreDataAccessManager
import com.pingwinek.jens.cookandbake.models.Tag4RecipeFB
import java.util.LinkedList

class Tag4RecipeSourceFB private constructor(private val firestore: FirebaseFirestore): Tag4RecipeSource<Tag4RecipeFB> {

    private val auth: FirebaseAuth = Firebase.auth
    private val basePathTag: String = "/tag"
    private val basePathUser: String = "/user"

    override suspend fun getAll(): LinkedList<Tag4RecipeFB> {
        throw NotImplementedError("Not supported for Tag4Recipe")
    }

    override suspend fun getAllForRecipeId(recipeId: String) : LinkedList<Tag4RecipeFB> {
        var list = LinkedList<Tag4RecipeFB>()

        if (auth.currentUser != null && auth.currentUser!!.isEmailVerified) {
            list = getAllForRecipeID(buildTagsByRecipeCollRef(auth.uid!!, recipeId), recipeId)
        } else {
            Log.i(this::class.java.name, "unauthorized getAllForRecipeId")
        }

        return list
    }

    override suspend fun get(id: String): Tag4RecipeFB? {
        throw NotImplementedError("Not supported by Firebase")
    }

    override suspend fun delete(item: Tag4RecipeFB): Boolean {
        var returnVal: Boolean = false

        if (auth.currentUser != null && auth.currentUser!!.isEmailVerified) {
            returnVal = delete(buildTagByRecipeDocRef(auth.uid!!, item.recipeID, item.id))
        } else {
            Log.i(this::class.java.name, "unauthorized delete")
        }

        return returnVal
    }

    override suspend fun update(item: Tag4RecipeFB): Tag4RecipeFB? {
        throw NotImplementedError("Not supported for Tag4Recipe")
    }

    override suspend fun new(item: Tag4RecipeFB): Tag4RecipeFB {
        var tag = item

        if (auth.currentUser != null && auth.currentUser!!.isEmailVerified) {
            if (item.id.isNotEmpty()) {
                tag = new(buildTagByRecipeDocRef(auth.uid!!, item.recipeID, item.id), item)
            } else {
                throw Exception("empty tagID")
            }
        } else {
            Log.i(this::class.java.name, "unauthorized delete")
        }

        return tag
    }

    private suspend fun getAllForRecipeID(collRef: CollectionReference, recipeID: String) : LinkedList<Tag4RecipeFB> {
        return FirestoreDataAccessManager.getAll(collRef) {
            Tag4RecipeFB(it, recipeID)
        }
    }

    private suspend fun get(documentReference: DocumentReference, recipeID: String) : Tag4RecipeFB {
        return FirestoreDataAccessManager.get(documentReference) {
            Tag4RecipeFB(it, recipeID)
        }
    }

    private suspend fun new(docRef: DocumentReference, tagFB: Tag4RecipeFB) : Tag4RecipeFB {
        return FirestoreDataAccessManager.update(docRef, tagFB.documentData) {
            Tag4RecipeFB(it, tagFB.recipeID)
        }
    }

    private suspend fun delete(docRef: DocumentReference) : Boolean {
        return FirestoreDataAccessManager.delete(docRef) {
            true
        }
    }

    private fun buildRecipesCollRef(userID: String) : CollectionReference {
        if (userID.isEmpty()) throw IllegalArgumentException("userID size 0 not allowed")
        return firestore.collection(basePathUser).document(userID).collection("recipe")
    }

    private fun buildTagsByRecipeCollRef(userID: String, recipeID: String) : CollectionReference {
        if (recipeID.isEmpty()) throw IllegalArgumentException("recipeID size 0 not allowed")
        return buildRecipesCollRef(userID).document(recipeID).collection("tag")
    }

    private fun buildTagByRecipeDocRef(userID: String, recipeID: String, tagID: String) : DocumentReference {
        if (tagID.isEmpty()) throw IllegalArgumentException("tagID size 0 not allowed")
        return buildTagsByRecipeCollRef(userID, recipeID).document(tagID)
    }

    companion object : SingletonHolder<Tag4RecipeSourceFB, FirebaseFirestore>(::Tag4RecipeSourceFB)
}