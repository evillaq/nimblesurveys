package com.vidixmx.nimblesurveys.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitService {

    private const val BASE_URL_STAGING = "https://survey-api-staging.nimblehq.co/api/v1/"
    private const val BASE_URL = "https://survey-api.nimblehq.co/api/v1/"
    private const val MOCK_SERVER = "https://nimble-survey-web-mock.fly.dev/"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val nimbleSurveyApi: NimbleSurveyApi by lazy {
        retrofit.create(NimbleSurveyApi::class.java)
    }

}