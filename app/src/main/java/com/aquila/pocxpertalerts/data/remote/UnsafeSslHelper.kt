package com.aquila.pocxpertalerts.data.remote

import okhttp3.OkHttpClient
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

/**
 * Provides an OkHttpClient and SSL helpers that bypass certificate validation.
 *
 * NOTE: Use only for internal/enterprise servers where the cert chain is
 * known to be untrustworthy (expired, self-signed, or hostname mismatch).
 * Do NOT use this in consumer-facing apps or against arbitrary public URLs.
 */
object UnsafeSslHelper {

    val trustAllManager: X509TrustManager = object : X509TrustManager {
        override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) = Unit
        override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) = Unit
        override fun getAcceptedIssuers(): Array<X509Certificate> = emptyArray()
    }

    val sslSocketFactory: SSLSocketFactory by lazy {
        SSLContext.getInstance("TLS").apply {
            init(null, arrayOf<TrustManager>(trustAllManager), SecureRandom())
        }.socketFactory
    }

    /** OkHttpClient that trusts all certificates and accepts any hostname. */
    val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .sslSocketFactory(sslSocketFactory, trustAllManager)
            .hostnameVerifier { _, _ -> true }
            .build()
    }
}
