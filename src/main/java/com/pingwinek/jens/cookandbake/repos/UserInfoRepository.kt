package com.pingwinek.jens.cookandbake.repos

import android.util.Log
import com.pingwinek.jens.cookandbake.PingwinekCooksApplication
import com.pingwinek.jens.cookandbake.lib.firestore.SuspendedCoroutineWrapper
import com.pingwinek.jens.cookandbake.models.UserInfo
import com.pingwinek.jens.cookandbake.models.UserInfoFB
import com.pingwinek.jens.cookandbake.sources.UserInfoSourceFB
import com.pingwinek.jens.cookandbake.utils.SingletonHolder
import java.util.LinkedList

class UserInfoRepository private constructor(val application: PingwinekCooksApplication) {

    private val userInfoSourceFB = application.getServiceLocator().getService(UserInfoSourceFB::class.java)

    suspend fun delete(userInfo: UserInfo) {
        userInfoSourceFB.delete(userInfo as UserInfoFB)
    }

    suspend fun getAll(): LinkedList<UserInfo> {
        return try {
            LinkedList<UserInfo>(userInfoSourceFB.getAll())
        } catch (exception: SuspendedCoroutineWrapper.SuspendedCoroutineException) {
            Log.e(this::class.java.name, exception.toString())
            LinkedList<UserInfo>()
        } catch (exception: Exception) {
            Log.e(this::class.java.name, exception.toString())
            LinkedList<UserInfo>()
        }
    }

    suspend fun get(id: String): UserInfo {
        return try {
            userInfoSourceFB.get(id)
        } catch (exception: SuspendedCoroutineWrapper.SuspendedCoroutineException) {
            Log.e(this::class.java.name, exception.toString())
            UserInfoFB(false)
        } catch (exception: Exception) {
            Log.e(this::class.java.name, exception.toString())
            UserInfoFB(false)
        }
    }


    suspend fun newUserInfo(crashlyticsEnabled: Boolean) : UserInfoFB {
        return try {
            userInfoSourceFB.new(UserInfoFB(crashlyticsEnabled))
        } catch (exception: SuspendedCoroutineWrapper.SuspendedCoroutineException) {
            Log.e(this::class.java.name, exception.toString())
            UserInfoFB(false)
        } catch (exception: Exception) {
            Log.e(this::class.java.name, exception.toString())
            UserInfoFB(false)
        }
    }

    suspend fun updateUserInfo(
        userInfo: UserInfo,
        crashlyticsEnabled: Boolean
    ): UserInfo {
        return try {
            userInfoSourceFB.update(UserInfoFB(userInfo.id, crashlyticsEnabled))
        } catch (exception: SuspendedCoroutineWrapper.SuspendedCoroutineException) {
            Log.e(this::class.java.name, exception.toString())
            userInfo
        } catch (exception: Exception) {
            Log.e(this::class.java.name, exception.toString())
            userInfo
        }
    }

    companion object : SingletonHolder<UserInfoRepository, PingwinekCooksApplication>(::UserInfoRepository)

}