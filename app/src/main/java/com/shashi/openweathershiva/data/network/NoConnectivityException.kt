package com.shashi.openweathershiva.data.network

import java.io.IOException

class NoConnectivityException : IOException() {
    // You can send any message whatever you want from here.
    override val message: String
        get() = "No network available, please check your WiFi or Data connection"
    // You can send any message whatever you want from here.
}