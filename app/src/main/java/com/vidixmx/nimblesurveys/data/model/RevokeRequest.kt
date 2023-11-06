package com.vidixmx.nimblesurveys.data.model

import com.google.gson.annotations.SerializedName

data class RevokeRequest(
    val token: String,
    @SerializedName("client_id") val clientId: String,
    @SerializedName("client_secret") val clientSecret: String,
)
