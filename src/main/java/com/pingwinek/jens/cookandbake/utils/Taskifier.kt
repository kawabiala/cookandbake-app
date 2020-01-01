package com.pingwinek.jens.cookandbake.utils

import android.os.AsyncTask
import android.util.Log
import java.lang.Exception

class Taskifier<T>(val onResult: (T?) -> Unit) : AsyncTask<() -> T?, Any, T>() {

    override fun doInBackground(vararg params: () -> T?): T? {
        return if (params.isNotEmpty()) {
            try {
                params[0]()
            } catch (e: Exception) {
                Log.w(this::class.java.name, e.localizedMessage)
                null
            }
        } else {
            null
        }
    }

    override fun onPostExecute(result: T?) {
        onResult(result)
    }
}