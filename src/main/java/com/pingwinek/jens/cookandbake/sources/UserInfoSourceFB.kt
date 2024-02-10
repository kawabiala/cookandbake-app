package com.pingwinek.jens.cookandbake.sources

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
    //private val basePath: String = "/user"

    override suspend fun getAll(): LinkedList<UserInfoFB> {
        if (!isAuthenticated()) throw Exception("unauthorized getAll")
        return Companion.getAll(buildUserInfosCollRef(auth.uid!!))
    }

    override suspend fun get(id: String) : UserInfoFB {
        if (!isAuthenticated()) throw Exception("unauthorized get")
        return get(buildUserInfoDocRef(auth.uid!!, id))
    }

    /**
     *
     */
    override suspend fun new(item: UserInfoFB) : UserInfoFB {
        if (!isAuthenticated()) throw Exception("unauthorized new")
        return add(buildUserInfosCollRef(auth.uid!!), item)
    }

    /**
     */
    override suspend fun update(item: UserInfoFB) : UserInfoFB {
        if (!isAuthenticated()) throw Exception("unauthorized update")
        return update(buildUserInfoDocRef(auth.uid!!, item.id), item)
    }

    override suspend fun delete(item: UserInfoFB) : Boolean {
        if (!isAuthenticated()) throw Exception("unauthorized delete")
        return delete(buildUserInfoDocRef(auth.uid!!, item.id))
    }


    companion object : SingletonHolder<UserInfoSourceFB, FirebaseFirestore>(::UserInfoSourceFB) {

        //private val firestore = FirebaseFirestore.getInstance()
        private const val BASEPATH = "/user"
        private const val USERINFOPATH = "userInfo"

        private fun isAuthenticated(): Boolean {
            return (Firebase.auth.currentUser != null)
        }

        private fun buildUserInfosCollRef(userID: String) : CollectionReference {
            return FirebaseFirestore.getInstance().collection(BASEPATH).document(userID).collection(USERINFOPATH)
        }

        private fun buildUserInfoDocRef(userID: String, userInfoID: String) : DocumentReference {
            return buildUserInfosCollRef(userID).document(userInfoID)
        }

        private suspend fun getAll(collRef: Query) : LinkedList<UserInfoFB> {
            return FirestoreDataAccessManager.getAll(collRef) {
                UserInfoFB(it)
            }
        }

        private suspend fun get(docRef: DocumentReference) : UserInfoFB {
            return FirestoreDataAccessManager.get(docRef) {
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
    }
}