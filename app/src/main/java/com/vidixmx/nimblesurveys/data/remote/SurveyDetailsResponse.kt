package com.vidixmx.nimblesurveys.data.remote


import com.google.gson.annotations.SerializedName
import com.vidixmx.nimblesurveys.data.model.Survey

data class SurveyDetailsResponse(
    val data: Data,
) {
    data class Data(
        @SerializedName("attributes")
        val survey: Survey,
        val id: String,
        val type: String,
    )
}