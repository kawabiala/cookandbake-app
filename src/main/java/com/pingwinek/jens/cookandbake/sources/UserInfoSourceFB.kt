package com.pingwinek.jens.cookandbake.sources

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import com.pingwinek.jens.cookandbake.lib.firestore.FirestoreDataAccessManager
import com.pingwinek.jens.cookandbake.models.UserInfoFB
import com.pingwinek.jens.cookandbake.utils.SingletonHolder
import java.util.LinkedList

/**
 * Source to retrieve and manipulate userInfos from Firebase
 *
 * @property firestore
 */
class UserInfoSourceFB private constructor(private val firestore: FirebaseFirestore) :
    UserInfoSource<UserInfoFB> {

    private val auth: FirebaseAuth = Firebase.auth
    private val basePath: String = "/user"

    override suspend fun getAll(): LinkedList<UserInfoFB> {
        var list = LinkedList<UserInfoFB>()
        if (auth.currentUser != null && auth.currentUser!!.isEmailVerified) {
            list = getAll(buildUserInfosCollRef(auth.uid!!))
        } else {
            Log.w(this::class.java.name, "unauthorized getAll")
        }
        return list
    }

    override suspend fun get(id: String) : UserInfoFB {
        var userInfoFB = UserInfoFB("",false,0)
        if (auth.currentUser != null && auth.currentUser!!.isEmailVerified) {
            userInfoFB = get(buildUserInfoDocRef(auth.uid!!, id))
        } else {
            Log.w(this::class.java.name, "unauthorized get")
        }
        return userInfoFB
    }

    /**
     *
     */
    override suspend fun new(item: UserInfoFB) : UserInfoFB {
        var userInfoFB: UserInfoFB = item
        if (auth.currentUser != null && auth.currentUser!!.isEmailVerified) {
            userInfoFB = add(buildUserInfosCollRef(auth.uid!!), item)
        } else {
            Log.w(this::class.java.name, "unauthorized new")
        }
        return userInfoFB
    }

    /**
     * Returns null if for any reason there is no corresponding item to be updated
     */
    override suspend fun update(item: UserInfoFB) : UserInfoFB {
        var userInfoFB: UserInfoFB = item
        if (auth.currentUser != null && auth.currentUser!!.isEmailVerified) {
            userInfoFB = update(buildUserInfoDocRef(auth.uid!!, item.id), item)
        } else {
            Log.w(this::class.java.name, "unauthorized update")
        }
        return userInfoFB
    }

    override suspend fun delete(item: UserInfoFB) : Boolean {
        return if (auth.currentUser != null && auth.currentUser!!.isEmailVerified) {
            delete(buildUserInfoDocRef(auth.uid!!, item.id))
        } else {
            false
        }
    }

    private fun buildUserInfosCollRef(userID: String) : CollectionReference {
        return firestore.collection(basePath).document(userID).collection("userInfo")
    }

    private fun buildUserInfoDocRef(userID: String, userInfoID: String) : DocumentReference {
        return buildUserInfosCollRef(userID).document(userInfoID)
    }

    private suspend fun get(docRef: DocumentReference) : UserInfoFB {
        return FirestoreDataAccessManager.get(docRef) {
            UserInfoFB(it)
        }
    }

    private suspend fun getAll(collRef: Query) : LinkedList<UserInfoFB> {
        return FirestoreDataAccessManager.getAll(collRef) {
            UserInfoFB(it)
        }
    }

    private suspend fun add(collRef: CollectionReference, userInfoFB: UserInfoFB) : UserInfoFB {
        return FirestoreDataAccessManager.new(collRef, userInfoFB.documentData) {
            UserInfoFB(it)
        }
    }

    private suspend fun update(docRef: DocumentReference, userInfoFB: UserInfoFB) : UserInfoFB {
        return FirestoreDataAccessManager.update(docRef, userInfoFB.documentData) {
            UserInfoFB(it)
        }
    }

    private suspend fun delete(docRef: DocumentReference) : Boolean {
        return FirestoreDataAccessManager.delete(docRef) {
            true
        }
    }

    companion object : SingletonHolder<UserInfoSourceFB, FirebaseFirestore>(::UserInfoSourceFB)
}