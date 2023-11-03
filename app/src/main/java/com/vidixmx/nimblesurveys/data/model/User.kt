package com.vidixmx.nimblesurveys.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val email: String,
    val name: String,
    val password: String? = null,
    @SerializedName("password_confirmation") val passwordConfirmation: String? = password,
    @SerializedName("avatar_url") val avatarUrl: String = "",
) : Parcelable