package com.pingwinek.jens.cookandbake

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import org.junit.Test
import org.mockito.Mockito.mock

class AuthServiceTest {

    private val application = mock(PingwinekCooksApplication::class.java)
    private val authService = AuthService.getInstance(application)

    @Test
    fun changePasswordTest() {
        CoroutineScope(Dispatchers.Default).launch {
            authService.changePassword("old", "new")
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun loginTest() {
        println("login")
        TestCoroutineScope(TestCoroutineDispatcher()).launch {
            println("login")
            val response = authService.login("email", "password")
            println("login ${response.code}")
        }
    }
}