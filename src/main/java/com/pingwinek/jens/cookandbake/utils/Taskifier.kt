package com.pingwinek.jens.cookandbake.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class Taskifier<T>(val onResult: (T?) -> Unit) {

    fun execute(doInBackground: () -> T?) {
        runBlocking {
            launch {
                doInBackground(doInBackground)
            }
        }
    }

    private suspend fun doInBackground(doInBackground: () -> T?) =
        withContext(Dispatchers.IO) {
        onResult(doInBackground())
    }
}