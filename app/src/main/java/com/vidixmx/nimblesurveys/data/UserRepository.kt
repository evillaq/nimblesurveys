package com.vidixmx.nimblesurveys.data

import com.vidixmx.nimblesurveys.Constants
import com.vidixmx.nimblesurveys.data.model.User
import com.vidixmx.nimblesurveys.data.remote.TokenResponse
import com.vidixmx.nimblesurveys.data.remote.NimbleSurveyApi
import com.vidixmx.nimblesurveys.data.remote.NimbleSurveyApi.Argument
import com.vidixmx.nimblesurveys.data.remote.NimbleSurveyApi.GrantType
import com.vidixmx.nimblesurveys.data.model.RegistrationRequest
import com.vidixmx.nimblesurveys.data.remote.UserProfileResponse
import retrofit2.Response

class UserRepository(
    private val api: NimbleSurveyApi,
) {

    suspend fun registerUser(user: User): retrofit2.Response<String> = api.registerAccount(
        RegistrationRequest(
            user = user,
            clientId = Constants.API_KEY,
            apiSecret = Constants.API_SECRET
        )
    )

    suspend fun loginUser(email: String, password: String): Response<TokenResponse> {

        val loginArgsMap = mutableMapOf(
            Argument.GRANT_TYPE to GrantType.PASSWORD,
            Argument.EMAIL to email,
            Argument.PASSWORD to password,
            Argument.CLIENT_ID to Constants.API_KEY,
            Argument.CLIENT_SECRET to Constants.API_SECRET
        )
        return api.login(loginArgsMap)

    }

    suspend fun getUserProfile(token: String): Response<UserProfileResponse> {
        val authorizationHeaderValue = "Bearer $token"
        return api.getUserProfile(authorizationHeaderValue)
    }

}