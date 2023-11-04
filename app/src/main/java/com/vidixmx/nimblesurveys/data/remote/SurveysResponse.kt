package com.vidixmx.nimblesurveys.data.remote


import com.google.gson.annotations.SerializedName
import com.vidixmx.nimblesurveys.data.model.Survey

data class SurveysResponse(
    @SerializedName("data")
    val surveys: List<Survey>,
    @SerializedName("meta")
    val meta: Meta,
) {
    data class Meta(
        @SerializedName("page")
        val page: Int,
        @SerializedName("page_size")
        val pageSize: Int,
        @SerializedName("pages")
        val pages: Int,
        @SerializedName("records")
        val records: Int,
    )
}