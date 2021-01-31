package com.pingwinek.jens.cookandbake.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.pingwinek.jens.cookandbake.AuthService
import com.pingwinek.jens.cookandbake.PingwinekCooksApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AuthenticationViewModel(application: Application) : AndroidViewModel(application) {

    private val authService = (application as PingwinekCooksApplication).getServiceLocator()
        .getService(AuthService::class.java)

    val response = MutableLiveData<AuthService.AuthenticationResponse>()

    fun getStoredAccount() = authService.getStoredAccount()
    fun hasStoredAccount() = authService.hasStoredAccount()
    fun isLoggedIn() = authService.isLoggedIn()

    fun changePassword(oldPassword: String, newPassword: String) {
        viewModelScope.launch(Dispatchers.IO) {
            response.postValue(authService.changePassword(oldPassword, newPassword))
        }
    }

    fun confirmRegistration(tempCode: String) {
        viewModelScope.launch(Dispatchers.IO) {
            response.postValue(authService.confirmRegistration(tempCode))
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            response.postValue(authService.login(email, password))
        }
    }

    fun logout() {
        viewModelScope.launch(Dispatchers.IO) {
            response.postValue(authService.logout())
        }
    }

    fun lostPassword(email: String) {
        viewModelScope.launch(Dispatchers.IO) {
            response.postValue(authService.lostPassword(email))
        }
    }

    fun newPassword(tempCode: String, newPassword: String) {
        viewModelScope.launch(Dispatchers.IO) {
            response.postValue(authService.newPassword(tempCode, newPassword))
        }
    }

    fun register(email: String, password: String, dataprotection: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            response.postValue(authService.register(email, password, dataprotection))
        }
    }

    fun unsubscribe(password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            response.postValue(authService.unsubscribe(password))
        }
    }
}