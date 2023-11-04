package com.vidixmx.nimblesurveys.data.remote

import com.vidixmx.nimblesurveys.data.model.RegistrationRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.QueryMap

interface NimbleSurveyApi {

    @GET("me")
    suspend fun getUserProfile(
        @Header("Authorization") token: String,
    ): Response<UserProfileResponse>

    @FormUrlEncoded
    @POST("oauth/token")
    suspend fun login(
        @FieldMap params: Map<String, String>,
    ): Response<TokenResponse>

    @POST("registrations")
    suspend fun registerAccount(
        @Body request: RegistrationRequest,
    ): Response<String>

    @POST("oauth/revoke")
    suspend fun logout(
        @FieldMap params: Map<String, String>,
    ): Response<String>

    @GET("surveys")
    suspend fun getSurveys(
        @Header("Authorization") token: String,
        @QueryMap params: Map<String, String>,
    ): Response<SurveysResponse>

    @GET("surveys/{id}")
    suspend fun getSurveyDetails(
        @Header("Authorization") token: String,
        @Path("id") surveyId: String,
    ): Response<SurveysResponse>

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
