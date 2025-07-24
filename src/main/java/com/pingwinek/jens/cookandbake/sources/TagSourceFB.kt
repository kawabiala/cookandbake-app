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
import com.pingwinek.jens.cookandbake.lib.firestore.SuspendedCoroutineWrapper
import com.pingwinek.jens.cookandbake.models.Tag
import com.pingwinek.jens.cookandbake.models.TagFB
import java.util.LinkedList

class TagSourceFB private constructor(private val firestore: FirebaseFirestore)
    : TagSource<TagFB>
{
    data class RecipeIDDocumentData(
        val recipeID: String
    )

    private val auth: FirebaseAuth = Firebase.auth
    private val basePathTag: String = "/tag"
    private val basePathUser: String = "/user"

    private val suspendedCoroutineExceptionCallback: (SuspendedCoroutineWrapper.SuspendedCoroutineException) -> Unit = { exception ->
        Log.e(this::class.java.name, exception.stackTraceToString())
    }

    override suspend fun getAll(): LinkedList<TagFB> {
        var list = LinkedList<TagFB>()

        if (auth.currentUser != null && auth.currentUser!!.isEmailVerified) {
            list = getAll(buildTagsCollRef(auth.uid!!))
        } else {
            Log.i(this::class.java.name, "unauthorized getAll")
        }

        return list
    }

    override suspend fun get(id: String): TagFB? {
        var tag: TagFB? = null

        if (auth.currentUser != null && auth.currentUser!!.isEmailVerified) {
            tag = get(buildTagDocRef(auth.uid!!, id))
        } else {
            Log.i(this::class.java.name, "unauthorized get")
        }

        return tag
    }

    override suspend fun delete(item: TagFB): Boolean {
        var returnVal = false

        if (auth.currentUser != null && auth.currentUser!!.isEmailVerified) {
            returnVal = delete(buildTagDocRef(auth.uid!!, item.id))
        } else {
            Log.i(this::class.java.name, "unauthorized delete")
        }

        return returnVal
    }

    override suspend fun update(item: TagFB): TagFB? {
        var returnVal: TagFB? = null

        if (auth.currentUser != null && auth.currentUser!!.isEmailVerified) {
            returnVal = update(buildTagDocRef(auth.uid!!, item.id), item)
        } else {
            Log.i(this::class.java.name, "unauthorized update")
        }

        return returnVal
    }

    suspend fun new(tag: Tag): TagFB {
        return new(TagFB(tag))
    }

    override suspend fun new(item: TagFB): TagFB {
        var tag = item

        if (auth.currentUser != null && auth.currentUser!!.isEmailVerified) {
            tag = new(buildTagsCollRef(auth.uid!!), item)
        } else {
            Log.i(this::class.java.name, "unauthorized new")
        }

        return tag
    }
/*
    override suspend fun getRecipeIDs(tag: Tag): LinkedList<String> {
        var list = LinkedList<String>()

        if (auth.currentUser != null && auth.currentUser!!.isEmailVerified) {
            list = getRecipes(buildRecipesByTagCollRef(auth.uid!!, tag.id))
        } else {
            Log.i(this::class.java.name, "unauthorized getRecipeIDs")
        }

        return list
    }

    override suspend fun newRecipeID(tag4Recipe: Tag4Recipe, recipeTitle: String): String {
        var returnValue: String = ""

        if (auth.currentUser != null && auth.currentUser!!.isEmailVerified) {
//            recipeID = newRecipeID(buildRecipesByTagCollRef(auth.uid!!, tagID), recipeID)
            returnValue = newRecipeID(buildRecipeDocRef(auth.uid!!, tag4Recipe.id, tag4Recipe.recipeID), recipeTitle)
        } else {
            Log.i(this::class.java.name, "unauthorized newRecipeID")
        }

        return returnValue
    }

    override suspend fun deleteRecipeID(tagID: String, recipeID: String): Boolean {
        var returnVal = false

        if (auth.currentUser != null && auth.currentUser!!.isEmailVerified) {
            returnVal = deleteRecipeID(buildRecipeDocRef(auth.uid!!, tagID, recipeID))
        } else {
            Log.i(this::class.java.name, "unauthorized deleteRecipeID")
        }

        return returnVal
    }
*/
    private suspend fun getAll(collRef: CollectionReference) : LinkedList<TagFB> {
        return FirestoreDataAccessManager.getAll(suspendedCoroutineExceptionCallback, collRef) {
            TagFB(it)
        } ?: LinkedList<TagFB>()
    }

    private suspend fun get(documentReference: DocumentReference) : TagFB {
        return FirestoreDataAccessManager.get(suspendedCoroutineExceptionCallback, documentReference) {
            TagFB(it)
        } ?: TagFB("")
    }

    private suspend fun new(collRef: CollectionReference, tagFB: TagFB) : TagFB {
//        Log.i(this::class.java.name, "new: " + tagFB.label)
        return FirestoreDataAccessManager.new(suspendedCoroutineExceptionCallback, collRef, tagFB.documentData) {
            TagFB(it)
        } ?: tagFB
    }

    private suspend fun delete(docRef: DocumentReference) : Boolean {
        return FirestoreDataAccessManager.delete(suspendedCoroutineExceptionCallback, docRef) {
            true
        }
    }

    private suspend fun update(docRef: DocumentReference, tagFB: TagFB) : TagFB? {
        return FirestoreDataAccessManager.update(suspendedCoroutineExceptionCallback, docRef, tagFB.documentData) {
            TagFB(it)
        }
    }
/*
    private suspend fun getRecipes(collRef: CollectionReference) : LinkedList<String> {
        return FirestoreDataAccessManager.getAll(collRef) {
            it.id
        }
    }

    private suspend fun newRecipeID(docRef: DocumentReference, recipeID: String): String {
        return FirestoreDataAccessManager.update(docRef, RecipeIDDocumentData(recipeID)) {
            it.id
        }
/*
        return FirestoreDataAccessManager.newWithID(collRef, RecipeIDDocumentData(recipeID), recipeID) {
            it.id
        }*/
    }

    private suspend fun deleteRecipeID(docRef: DocumentReference): Boolean {
        return FirestoreDataAccessManager.delete(docRef) {
            true
        }
    }
*/
    //TODO handle exceptions
    private fun buildTagsCollRef(userID: String) : CollectionReference {
        return firestore.collection(basePathUser).document(userID).collection(basePathTag)
    }

    private fun buildTagDocRef(userID: String, tagID: String) : DocumentReference {
        if (tagID.isEmpty()) throw IllegalArgumentException("tagID size 0 not allowed")
        return buildTagsCollRef(userID).document(tagID)
    }

    private fun buildRecipesByTagCollRef(userID: String, tagID: String) : CollectionReference {
        if (tagID.isEmpty()) throw IllegalArgumentException("tagID size 0 not allowed")
        return buildTagDocRef(userID, tagID).collection("recipe")
    }

    private fun buildRecipeDocRef(userID: String, tagID: String, recipeID: String) : DocumentReference {
        if (recipeID.isEmpty()) throw IllegalArgumentException("recipeID size 0 not allowed")
        return buildRecipesByTagCollRef(userID, tagID).document(recipeID)
    }

    companion object : SingletonHolder<TagSourceFB, FirebaseFirestore>(::TagSourceFB)

}