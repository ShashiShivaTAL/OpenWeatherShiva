package com.shashi.openweathershiva.data.responses

import androidx.annotation.Keep

@Keep
data class TokenResponse(
    val access_token: String?,
    val refresh_token: String?
)