package com.vidixmx.nimblesurveys.data.remote

import com.google.gson.annotations.SerializedName
import com.vidixmx.nimblesurveys.data.model.Token
import com.vidixmx.nimblesurveys.data.model.User

data class UserProfileResponse(
    val data: Data,
) {
    data class Data(
        @SerializedName("attributes")
        val userProfile: User,
        val id: String,
        val type: String,
    )
}

