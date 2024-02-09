package com.pingwinek.jens.cookandbake.repos

import android.util.Log
import com.pingwinek.jens.cookandbake.PingwinekCooksApplication
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
        return try{
            LinkedList<UserInfo>(userInfoSourceFB.getAll())
        } catch (exception: Exception) {
            Log.e(this::class.java.name, "Error when retrieving recipe list: $exception")
            LinkedList<UserInfo>()
        }
    }

    suspend fun get(id: String): UserInfo {
        return userInfoSourceFB.get(id)
    }


    suspend fun newUserInfo(
        crashlyticsEnabled: Boolean
    ) : UserInfoFB {
        return userInfoSourceFB.new(UserInfoFB(crashlyticsEnabled))
    }

    suspend fun updateUserInfo(
        userInfo: UserInfo,
        crashlyticsEnabled: Boolean
    ): UserInfo {
        return userInfoSourceFB.update(UserInfoFB(userInfo.id, crashlyticsEnabled))
    }

    companion object : SingletonHolder<UserInfoRepository, PingwinekCooksApplication>(::UserInfoRepository)

}