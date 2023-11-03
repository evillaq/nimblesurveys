package com.vidixmx.nimblesurveys.data.remote

import com.google.gson.annotations.SerializedName
import com.vidixmx.nimblesurveys.data.model.User

data class RegistrationRequest(
    val user: User,
    @SerializedName("client_id") val clientId: String,
    @SerializedName("client_secret") val apiSecret: String,
)
