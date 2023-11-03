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

    object GrantType {
        const val PASSWORD = "password"
        const val REFRESH_TOKEN = "refresh_token"
    }

    object Argument {
        const val GRANT_TYPE = "grant_type"
        const val EMAIL = "email"
        const val PASSWORD = "password"
        const val CLIENT_ID = "client_id"
        const val CLIENT_SECRET = "client_secret"
    }
}
