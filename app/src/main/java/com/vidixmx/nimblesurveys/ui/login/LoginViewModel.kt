package com.vidixmx.nimblesurveys.ui.login

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
import com.vidixmx.nimblesurveys.data.model.Token
import com.vidixmx.nimblesurveys.data.model.User
import com.vidixmx.nimblesurveys.data.remote.NimbleError
import com.vidixmx.nimblesurveys.utils.isTokenValid
import com.vidixmx.nimblesurveys.utils.sharedPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

typealias SurveyCredentials = Pair<String, String>

class LoginViewModel(
    application: Application,
    private val repository: UserRepository,
) : AndroidViewModel(application) {

    // preferences delegation
    private var accessToken by sharedPreferences(Preferences.ACCESS_TOKEN, "")
    private var refreshToken by sharedPreferences(Preferences.REFRESH_TOKEN, "")
    private var tokenCreation by sharedPreferences(Preferences.TOKEN_CREATION_TIMESTAMP, "")
    private var tokenExpiresInSeconds by sharedPreferences(Preferences.TOKEN_EXPIRES_IN_SECONDS, "")
    private var lastEmail by sharedPreferences(Preferences.LAST_EMAIL, "")
    private var lastPassword by sharedPreferences(Preferences.LAST_PASSWORD, "")

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    private fun setIsLoading(newValue: Boolean) {
        viewModelScope.launch {
            _isLoading.postValue(newValue)
        }
    }

    private val _user = MutableLiveData<User>()
    val user: LiveData<User> = _user

    fun loginUser(email: String, password: String) {

        if (validCredentials(email, password)) {
            setIsLoading(true)
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    val result = repository.loginUser(email, password)
                    withContext(Dispatchers.Main) {
                        if (result.isSuccessful) {
                            result.body()?.let { tokenResponse ->
                                processToken(tokenResponse.data.token)
                            }
                            // update preferences
                            lastEmail = email
                            lastPassword = password
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
                } finally {
                    setIsLoading(false)
                }
            }
        }

    }

    private fun fetchUserProfile() {

        viewModelScope.launch(Dispatchers.IO) {
            setIsLoading(true)
            try {
                val result = repository.getUserProfile(accessToken)
                withContext(Dispatchers.Main) {
                    if (result.isSuccessful) {
                        result.body()?.let { userProfileResponse ->
                            loadUserProfile(userProfileResponse.data.userProfile)
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
            } finally {
                setIsLoading(false)
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

    fun getLastUsedCredentials(): SurveyCredentials = SurveyCredentials(lastEmail, lastPassword)

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

    private fun processToken(token: Token) {

        // update preferences
        accessToken = token.accessToken
        refreshToken = token.refreshToken
        tokenCreation = token.createdAt.toString()
        tokenExpiresInSeconds = token.expiresIn.toString()

        // retrieve user data
        fetchUserProfile()
    }

    private fun loadUserProfile(userProfile: User) {
        val email = userProfile.email
        val name = userProfile.name
        val avatarUrl = userProfile.avatarUrl

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