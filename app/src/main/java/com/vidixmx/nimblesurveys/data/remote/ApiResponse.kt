package com.vidixmx.nimblesurveys.data.remote

import com.google.gson.annotations.SerializedName

data class ApiResponse(
    val data: Data,
) {
    data class Data(
        val attributes: Map<String, String>,
        val id: String,
        val type: String,
    ) {
        data class Attributes(
            @SerializedName("access_token")
            val accessToken: String,
            @SerializedName("created_at")
            val createdAt: Int,
            @SerializedName("expires_in")
            val expiresIn: Int,
            @SerializedName("refresh_token")
            val refreshToken: String,
            @SerializedName("token_type")
            val tokenType: String,
        )
    }
}

