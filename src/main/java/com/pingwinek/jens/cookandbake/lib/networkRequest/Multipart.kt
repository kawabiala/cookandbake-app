package com.pingwinek.jens.cookandbake.lib.networkRequest

import java.io.InputStream
import java.lang.StringBuilder
import java.nio.BufferUnderflowException
import java.nio.ByteBuffer
import java.nio.CharBuffer
import java.nio.charset.StandardCharsets
import java.util.*

class Multipart private constructor(parts: List<ByteBuffer>){

    private val bytes = LinkedList<Byte>()

    init {
        parts.forEach {
            addByteBuffer(it)
        }
        addByteBuffer(StandardCharsets.UTF_8.encode("$HYPHEN$BOUNDARY$HYPHEN$CRLF"))
    }

    companion object {
        const val HYPHEN = "--"
        const val CRLF = "\r\n"
        const val BOUNDARY = "XXXX0XXXX1XXXX2XXXX3"
    }

    class Builder {

        private val parts = mutableListOf<ByteBuffer>()

        fun addTextPart(fieldName: String, text: String) {
            val stringBuilder = StringBuilder()
            stringBuilder.append(HYPHEN).append(BOUNDARY).append(CRLF)
            stringBuilder.append("Content-Disposition: form-data").append(";")
                .append("name=\"$fieldName\"").append(CRLF)
            stringBuilder.append(CRLF)
            stringBuilder.append(text).append(CRLF)
            val buffer = StandardCharsets.UTF_8.encode(CharBuffer.wrap(stringBuilder))
            parts.add(buffer)
        }

        fun addFile(fieldName: String, fileName: String, contentType: NetworkRequest.ContentType, inputStream: InputStream) {
            val stringBuilder = StringBuilder()
            stringBuilder.append(HYPHEN).append(BOUNDARY).append(CRLF)
            stringBuilder.append("Content-Disposition: form-data").append(";")
                .append("name=\"$fieldName\"").append(";")
                .append("filename=\"$fileName\"")
                .append(CRLF)
            stringBuilder.append("Content-Type: ${contentType.contentType}").append(CRLF)
            stringBuilder.append("Content-Transfer-Encoding: binary").append(CRLF)
            stringBuilder.append(CRLF)
            val buffer = StandardCharsets.UTF_8.encode(CharBuffer.wrap(stringBuilder))
            parts.add(buffer)

            parts.add(ByteBuffer.wrap(inputStream.readBytes()))
            parts.add(StandardCharsets.UTF_8.encode(CRLF))
        }

        fun build() : Multipart {
            return Multipart(parts)
        }
    }

    fun getContentType() : NetworkRequest.ContentType {
        return NetworkRequest.ContentType.MULTIPART.addParam("boundary", BOUNDARY)
    }

    fun getContent() : ByteBuffer {
        return ByteBuffer.wrap(bytes.toByteArray())
    }

    private fun addByteBuffer(byteBuffer: ByteBuffer) {
        byteBuffer.rewind()
        try {
            while (true) {
                bytes.add(byteBuffer.get())
            }
        } catch (e: BufferUnderflowException) {

        }
    }
}