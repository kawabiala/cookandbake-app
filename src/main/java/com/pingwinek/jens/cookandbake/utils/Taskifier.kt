package com.pingwinek.jens.cookandbake.sources

import android.os.AsyncTask

class Taskifier<T>(val onResult: (T?) -> Unit) : AsyncTask<() -> T?, Any, T>() {

    override fun doInBackground(vararg params: () -> T?): T? {
        return if (params.isNotEmpty()) {
            params[0]()
        } else {
            null
        }
    }

    override fun onPostExecute(result: T?) {
        onResult(result)
    }
}