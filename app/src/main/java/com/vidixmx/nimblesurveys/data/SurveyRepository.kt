package com.vidixmx.nimblesurveys.data

import com.vidixmx.nimblesurveys.data.remote.NimbleSurveyApi
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
        val params = mapOf(
            "page" to pageNumber.toString(),
            "size" to pageSize.toString()
        )
        return api.getSurveys(token, params)
    }

}