package com.pingwinek.jens.cookandbake.lib.networkRequest

import org.chromium.net.CronetException
import org.chromium.net.UrlRequest
import org.chromium.net.UrlResponseInfo
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets

class NetworkRequestCallback(private val networkResponseRouter: NetworkResponseRouter) : UrlRequest.Callback() {

    private val capacity = 10000

    private val responseStringBuilder = StringBuilder()

    override fun onReadCompleted(request: UrlRequest?, info: UrlResponseInfo?, byteBuffer: ByteBuffer?) {
        byteBuffer?.flip()
        val str = StandardCharsets.UTF_8.decode(byteBuffer).toString()
        responseStringBuilder.append(str)

        request?.read(ByteBuffer.allocateDirect(capacity))
    }

    override fun onFailed(request: UrlRequest?, info: UrlResponseInfo?, error: CronetException?) {
        networkResponseRouter.routeResponse(AbstractNetworkResponseRoutes.Result.FAILED, info?.httpStatusCode ?: -1, error?.localizedMessage ?: "")
    }

    override fun onSucceeded(request: UrlRequest?, info: UrlResponseInfo?) {
        info?.let { _info ->
            _info.allHeaders?.get("Set-Cookie")?.let { _cookies ->
                CookieStore.setCookies(_info.url, _cookies)
            }
        }

        networkResponseRouter.routeResponse(AbstractNetworkResponseRoutes.Result.SUCCESS, info?.httpStatusCode ?: -1, responseStringBuilder.toString())
    }

    override fun onRedirectReceived(request: UrlRequest?, info: UrlResponseInfo?, newLocationUrl: String?) {
        request?.cancel()
    }

    override fun onResponseStarted(request: UrlRequest?, info: UrlResponseInfo?) {
        request?.read(ByteBuffer.allocateDirect(capacity))
    }

}