package com.vidixmx.nimblesurveys.data.remote


import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken

data class NimbleError(
    @SerializedName("errors")
    val errors: List<Error>,
) {
    data class Error(
        @SerializedName("code")
        val code: String,
        @SerializedName("detail")
        val detail: String,
    )

    companion object {
        fun fromString(string: String): NimbleError? =
            Gson().fromJson<NimbleError?>(
                string,
                object : TypeToken<NimbleError?>() {}.type
            )

    }
}
