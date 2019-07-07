package com.pingwinek.jens.cookandbake.networkRequest

import org.chromium.net.UploadDataProvider
import org.chromium.net.UploadDataSink
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets

class NetworkRequestBodyProvider(val body: String): UploadDataProvider() {

    override fun getLength(): Long {
        return body.length.toLong()
    }

    override fun rewind(uploadDataSink: UploadDataSink?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun read(uploadDataSink: UploadDataSink?, byteBuffer: ByteBuffer?) {
        byteBuffer?.put(body.toByteArray(StandardCharsets.UTF_8))
        uploadDataSink?.onReadSucceeded(false)
    }

}
