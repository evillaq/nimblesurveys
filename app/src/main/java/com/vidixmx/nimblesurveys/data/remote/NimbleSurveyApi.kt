package com.vidixmx.nimblesurveys.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface NimbleSurveyApi {

    @FormUrlEncoded
    @POST("oauth/token")
    suspend fun login(
        @FieldMap params: Map<String, String>,
    ): Response<ApiResponse>

    @POST("registrations")
    suspend fun registerAccount(
        @Body request: RegistrationRequest,
    ): Response<String>

}
