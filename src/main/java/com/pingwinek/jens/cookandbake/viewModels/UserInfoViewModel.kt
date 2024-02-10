package com.pingwinek.jens.cookandbake.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.pingwinek.jens.cookandbake.PingwinekCooksApplication
import com.pingwinek.jens.cookandbake.models.UserInfo
import com.pingwinek.jens.cookandbake.repos.UserInfoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserInfoViewModel(application: Application) : AndroidViewModel(application) {

    private val userInfoRepository = UserInfoRepository.getInstance(application as PingwinekCooksApplication)

    private val privateUserInfoData = MutableLiveData<UserInfo>()
    val userInfoData: LiveData<UserInfo>
        get() = privateUserInfoData

    var userInfoId: String? = null

    fun loadData() {
        userInfoId?.let { id ->
            viewModelScope.launch(Dispatchers.IO) {
                privateUserInfoData.postValue(userInfoRepository.get(id))
            }
        }
    }

    fun saveUserInfo(crashlyticsEnabled: Boolean) {
        userInfoData.value?.let { userInfo ->
            viewModelScope.launch(Dispatchers.IO) {
                privateUserInfoData.postValue(userInfoRepository.updateUserInfo(userInfo, crashlyticsEnabled))
            }
        } ?: run {
            viewModelScope.launch(Dispatchers.IO) {
                userInfoRepository.newUserInfo(crashlyticsEnabled).let { userInfo ->
                    userInfoId = userInfo.id
                    privateUserInfoData.postValue(userInfo)
                }
            }
        }
    }

}