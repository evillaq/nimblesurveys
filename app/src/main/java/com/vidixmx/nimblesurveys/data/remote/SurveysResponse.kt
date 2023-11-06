package com.vidixmx.nimblesurveys.data.remote


import com.google.gson.annotations.SerializedName
import com.vidixmx.nimblesurveys.data.model.Survey

data class SurveysResponse(
    val data: ArrayList<Data>,
    val meta: Map<String, Int>,
) {
    data class Data(
        @SerializedName("attributes")
        val survey: Survey,
        val id: String,
        val type: String,
    )

    fun toSurveyList(): List<Survey> {
        return this.data.map { data ->
            data.survey.apply {
                id = data.id
            }
        }
    }
}