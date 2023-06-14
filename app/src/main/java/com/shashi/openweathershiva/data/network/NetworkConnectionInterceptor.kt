package com.shashi.openweathershiva.data.network

import android.annotation.SuppressLint
import android.content.Context

import okhttp3.OkHttpClient
import kotlin.Throws
import android.net.ConnectivityManager
import android.os.Build
import android.net.NetworkCapabilities
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class NetworkConnectionInterceptor(private val mContext: Context) : OkHttpClient(), Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        if (!isConnected) {
            throw NoConnectivityException()
            // Throwing our custom exception 'NoConnectivityException'
        }
        val builder: Request.Builder = chain.request().newBuilder()
        return chain.proceed(builder.build())
    }

    private val isConnected: Boolean
        @SuppressLint("ObsoleteSdkInt")
        get() {
            val cm = mContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val capabilities = cm.getNetworkCapabilities(cm.activeNetwork)
                capabilities != null && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || capabilities.hasTransport(
                    NetworkCapabilities.TRANSPORT_CELLULAR
                ))
            } else {
                val activeNetwork = cm.activeNetworkInfo
                activeNetwork != null && activeNetwork.isConnected
            }
        }
}