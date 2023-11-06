package com.vidixmx.nimblesurveys.data

import com.vidixmx.nimblesurveys.data.remote.NimbleSurveyApi
import com.vidixmx.nimblesurveys.data.remote.SurveyDetailsResponse
import com.vidixmx.nimblesurveys.data.remote.SurveysResponse
import retrofit2.Response

class SurveyRepository(
    private val api: NimbleSurveyApi,
) {

    suspend fun getSurveys(
        token: String,
        pageNumber: Int = 1,
        pageSize: Int = 3,
    ): Response<SurveysResponse> {
        val authorizationHeaderValue = "Bearer $token"
        val params = mapOf(
            "number" to pageNumber,
            "size" to pageSize
        )
        return api.getSurveys(authorizationHeaderValue, params)
    }

    suspend fun getSurveyDetails(
        token: String,
        surveyId: String,
    ): Response<SurveyDetailsResponse> {
        val authorizationHeaderValue = "Bearer $token"
        return api.getSurveyDetails(authorizationHeaderValue, surveyId)
    }

}