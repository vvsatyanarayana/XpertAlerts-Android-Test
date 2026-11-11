
package com.aquila.pocxpertalerts.data.remote

import com.aquila.pocxpertalerts.BuildConfig
import okhttp3.OkHttpClient
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

/**
 * Insecure SSL helper for DEBUG builds only.
 *
 * Never use this client in a production/release build.
 */
object UnsafeSslHelper {

    private val trustAllManager: X509TrustManager =
        object : X509TrustManager {

            override fun checkClientTrusted(
                chain: Array<X509Certificate>,
                authType: String
            ) = Unit

            override fun checkServerTrusted(
                chain: Array<X509Certificate>,
                authType: String
            ) = Unit

            override fun getAcceptedIssuers(): Array<X509Certificate> =
                emptyArray()
        }

    val sslSocketFactory: SSLSocketFactory by lazy {
        check(BuildConfig.DEBUG) {
            "SSL certificate bypass is allowed only in Debug builds."
        }

        SSLContext.getInstance("TLS").apply {
            init(
                null,
                arrayOf<TrustManager>(trustAllManager),
                SecureRandom()
            )
        }.socketFactory
    }

    val okHttpClient: OkHttpClient by lazy {
        check(BuildConfig.DEBUG) {
            "Unsafe SSL client must not be used in Release builds."
        }

        OkHttpClient.Builder()
            .sslSocketFactory(
                sslSocketFactory,
                trustAllManager
            )
            .hostnameVerifier { _, _ -> true }
            .build()
    }
}
