package com.pingwinek.jens.cookandbake.lib.sync

class Promise<T> {

    enum class Status {
        SUCCESS, FAILURE
    }

    data class Result<T>(
        val status: Status,
        val value: T?
    )

    private var resultHandler: ((Result<T>) -> Unit)? = null
    private var result: Result<T>? = null
    private var sent = false

    fun setResultHandler(handler: (Result<T>) -> Unit) {
        if (resultHandler == null) {
            resultHandler = handler
            sendResult()
        }
    }

    fun setResult(status: Status, value: T?) {
        if (result == null) {
            result = Result(status, value)
            sendResult()
        }
    }

    @Synchronized
    private fun sendResult() {
        result?.let { nonNullResult ->
            resultHandler?.let { nonNullResultHandler ->
                if (!sent) {
                    nonNullResultHandler(nonNullResult)
                    sent = true
                }
            }
        }
    }
}