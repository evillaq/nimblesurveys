package com.vidixmx.nimblesurveys.data.model

import com.google.gson.annotations.SerializedName

data class User(
    val email: String,
    val name: String,
    val password: String,
    @SerializedName("password_confirmation") val passwordConfirmation: String = password,
    @SerializedName("avatar_url") val avatarUrl: String = "",
)