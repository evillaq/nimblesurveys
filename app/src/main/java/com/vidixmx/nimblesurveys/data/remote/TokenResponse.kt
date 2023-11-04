package com.vidixmx.nimblesurveys.data.remote

import com.google.gson.annotations.SerializedName
import com.vidixmx.nimblesurveys.data.model.Token

data class TokenResponse(
    val data: Data,
) {
    data class Data(
        @SerializedName("attributes")
        val token: Token,
        val id: String,
        val type: String,
    )
}

