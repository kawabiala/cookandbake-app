package com.pingwinek.jens.cookandbake

import android.app.Application
import android.content.Context
import android.content.SharedPreferences

class Account private constructor(application: Application, email: String) {

    private val prefs = application.getSharedPreferences(SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE)

    init {
        storePreference("email", email)
    }

    fun setRefreshToken(token: String) {
        storePreference("token", token)
    }

    fun getEmail() : String? {
        return retrievePreference("email")
    }

    fun getRefreshToken() : String? {
        return retrievePreference("token")
    }

    fun hasRefreshToken() : Boolean {
        return (getRefreshToken() != null)
    }

    private fun storePreference(key: String, value: String) {
        prefs.edit().apply {
            putString(key, value)
        }.apply()
    }

    private fun retrievePreference(key: String) : String? {
        return prefs.getString(key, null) ?: null
    }

    companion object {

        fun getStoredAccount(application: Application) : Account? {
            val email = getPrefs(application).getString("email", null) ?: null
            return email?.let { Account(application, it) }
        }

        fun createStoredAccount(application: Application, email: String) : Account {
            return Account(application, email)
        }

        fun createStoredAccount(application: Application, email: String, token: String) : Account {
            return Account(application, email).apply {
                setRefreshToken(token)
            }
        }

        private fun getPrefs(application: Application) : SharedPreferences {
            return application.getSharedPreferences(SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE)
        }
    }
}