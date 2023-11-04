package com.vidixmx.nimblesurveys.ui

import android.app.Application
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.vidixmx.nimblesurveys.Constants.Preferences
import com.vidixmx.nimblesurveys.data.UserRepository
import com.vidixmx.nimblesurveys.data.model.User
import com.vidixmx.nimblesurveys.data.remote.ApiResponse
import com.vidixmx.nimblesurveys.data.remote.NimbleError
import com.vidixmx.nimblesurveys.utils.isTokenValid
import com.vidixmx.nimblesurveys.utils.sharedPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginViewModel(
    application: Application,
    private val repository: UserRepository,
) : AndroidViewModel(application) {

    // preferences delegation
    private var accessToken by sharedPreferences(Preferences.ACCESS_TOKEN, "")
    private var refreshToken by sharedPreferences(Preferences.REFRESH_TOKEN, "")
    private var tokenCreation by sharedPreferences(Preferences.TOKEN_CREATION_TIMESTAMP, "")
    private var tokenExpiresInSeconds by sharedPreferences(Preferences.TOKEN_EXPIRES_IN_SECONDS, "")

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    private val _user = MutableLiveData<User>()
    val user: LiveData<User> = _user

    fun loginUser(email: String, password: String) {

        if (validCredentials(email, password)) {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    val result = repository.loginUser(email, password)
                    withContext(Dispatchers.Main) {
                        if (result.isSuccessful) {
                            result.body()?.let { loginResponse ->
                                processLoginResponseData(loginResponse.data)
                            }
                        } else {
                            val responseError: NimbleError? =
                                NimbleError.fromString(result.errorBody()!!.string())
                            responseError?.let { error ->
                                if (error.errors.isNotEmpty()) {
                                    when (error.errors[0].code) {
                                        "invalid_email_or_password" -> {
                                            _message.postValue(error.errors[0].detail)
                                        }

                                        else -> _message.postValue(error.errors[0].detail)
                                    }
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    _message.postValue("Error registering user")
                }
            }
        }

    }

    private fun fetchUserProfile() {

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = repository.getUserProfile(accessToken)
                withContext(Dispatchers.Main) {
                    if (result.isSuccessful) {
                        result.body()?.let { userProfileResponse ->
                            loadUserProfile(userProfileResponse.data)
                        }
                    } else {
                        val responseError: NimbleError? =
                            NimbleError.fromString(result.errorBody()!!.string())
                        responseError?.let { error ->
                            if (error.errors.isNotEmpty()) {
                                when (error.errors[0].code) {
                                    "invalid_email_or_password" -> {
                                        _message.postValue(error.errors[0].detail)
                                    }

                                    else -> _message.postValue(error.errors[0].detail)
                                }
                            }
                        }
                        println("Error: $responseError")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _message.postValue("Error registering user")
            }
        }

    }

    /**
     * If exist a stored token in preferences, check if it is still valid
     * considering a little margin of n seconds
     */
    fun validateToken() {
        if (tokenCreation != "" && tokenExpiresInSeconds != "") {
            if (isTokenValid(tokenCreation.toLong(), tokenExpiresInSeconds.toLong())) {
                fetchUserProfile()
            }
        }
    }

    // auxiliary functions

    private fun validCredentials(email: String, password: String): Boolean {
        return if (email.isEmpty()) {
            _message.postValue("Please enter your email")
            false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _message.postValue("Please enter a valid email")
            false
        } else if (password.isEmpty()) {
            _message.postValue("Please enter your password")
            false
        } else {
            true
        }
    }

    private fun processLoginResponseData(data: ApiResponse.Data) {

        // update preferences
        accessToken = data.attributes[Preferences.ACCESS_TOKEN] ?: ""
        refreshToken = data.attributes[Preferences.REFRESH_TOKEN] ?: ""
        tokenCreation = data.attributes[Preferences.TOKEN_CREATION_TIMESTAMP] ?: ""
        tokenExpiresInSeconds = data.attributes[Preferences.TOKEN_EXPIRES_IN_SECONDS] ?: ""

        // retrieve user data
        fetchUserProfile()
    }

    private fun loadUserProfile(data: ApiResponse.Data) {
        val email = data.attributes["email"] ?: ""
        val name = data.attributes["name"] ?: ""
        val avatarUrl = data.attributes["avatar_url"] ?: ""

        if (email.isEmpty() or name.isEmpty()) {
            throw IllegalStateException("Invalid user profile")
        } else {
            val user = User(email = email, name = name, avatarUrl = avatarUrl)
            _user.postValue(user)
        }
    }

    companion object {
        private const val TAG = "LoginViewModel"
    }

}

class LoginViewModelFactory(
    private val application: Application,
    private val repository: UserRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}