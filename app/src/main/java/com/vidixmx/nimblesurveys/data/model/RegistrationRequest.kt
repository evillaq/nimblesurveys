package com.vidixmx.nimblesurveys.data.model

import com.google.gson.annotations.SerializedName

data class RegistrationRequest(
    val user: User,
    @SerializedName("client_id") val clientId: String,
    @SerializedName("client_secret") val apiSecret: String,
)
