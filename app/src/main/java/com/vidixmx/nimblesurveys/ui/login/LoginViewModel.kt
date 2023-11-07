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
import com.vidixmx.nimblesurveys.Constants.TOKEN_DEFAULT_EXPIRATION_TIME_IN_SECONDS
import com.vidixmx.nimblesurveys.data.UserRepository
import com.vidixmx.nimblesurveys.data.model.Token
import com.vidixmx.nimblesurveys.data.model.User
import com.vidixmx.nimblesurveys.data.remote.NimbleError
import com.vidixmx.nimblesurveys.data.remote.RetrofitService
import com.vidixmx.nimblesurveys.data.remote.TokenResponse
import com.vidixmx.nimblesurveys.utils.isTokenValid
import com.vidixmx.nimblesurveys.utils.sharedPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

typealias SurveyCredentials = Pair<String, String>

class LoginViewModel(
    application: Application,
    private val repository: UserRepository,
) : AndroidViewModel(application) {

    // preferences delegation
    private var accessToken by sharedPreferences(Preferences.ACCESS_TOKEN, "")
    private var refreshToken by sharedPreferences(Preferences.REFRESH_TOKEN, "")
    private var tokenCreation by sharedPreferences(Preferences.TOKEN_CREATION_TIMESTAMP, "")
    private var tokenExpiresInSeconds by sharedPreferences(Preferences.TOKEN_EXPIRES_IN_SECONDS,
        TOKEN_DEFAULT_EXPIRATION_TIME_IN_SECONDS.toString())
    private var lastEmail by sharedPreferences(Preferences.LAST_EMAIL, "")
    private var lastPassword by sharedPreferences(Preferences.LAST_PASSWORD, "")

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    private fun setMessage(message: String) {
        viewModelScope.launch {
            _message.postValue(message)
        }
    }

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    private fun setIsLoading(newValue: Boolean) {
        viewModelScope.launch {
            _isLoading.postValue(newValue)
        }
    }

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?>
        get() {
            if (tokenCreation == "") {
                _user.value = null
            }
            return _user
        }

    private fun setUser(user: User?) {
        viewModelScope.launch {
            _user.postValue(user)
        }
    }

    fun loginUser(email: String, password: String) {
        requestToken(email = email, password = password)
    }

    private fun requestTokenRefresh() {
        requestToken(refreshToken = refreshToken)
    }

    private fun requestToken(
        email: String = "",
        password: String = "",
        refreshToken: String = "",
    ) {

        // validate credentials
        if (refreshToken.isEmpty() && !validCredentials(email, password)) {
            return
        }

        setIsLoading(true)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result: Response<TokenResponse> = if (refreshToken.isEmpty()) {
                    repository.loginUser(email, password)
                } else {
                    repository.refreshToken(refreshToken)
                }

                if (result.isSuccessful) {
                    result.body()?.let { tokenResponse ->
                        updatePreferences(tokenResponse.data.token)
                        fetchUserProfile()
                    }
                    // update preferences
                    if (refreshToken.isEmpty()) {
                        lastEmail = email
                        lastPassword = password
                    }
                } else {
                    val responseError: NimbleError? =
                        NimbleError.fromString(result.errorBody()!!.string())
                    responseError?.let { error ->
                        if (error.errors.isNotEmpty()) {
                            setMessage(error.errors[0].detail)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                setMessage("Error registering user")
            } finally {
                setIsLoading(false)
            }
        }

    }

    private fun fetchUserProfile() {

        viewModelScope.launch(Dispatchers.IO) {
            setIsLoading(true)
            try {
                val result = repository.getUserProfile(accessToken)
                if (result.isSuccessful) {
                    result.body()?.let { userProfileResponse ->
                        loadUserProfile(userProfileResponse.data.userProfile)
                    }
                } else {
                    val responseError: NimbleError? =
                        NimbleError.fromString(result.errorBody()!!.string())
                    responseError?.let { error ->
                        if (error.errors.isNotEmpty()) {
                            setMessage(error.errors[0].detail)
                        }
                    }
                    setUser(null)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                setMessage("Error registering user")
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
        if (tokenCreation != "") {
            // if token is no longer valid, request a new one
            if (!isTokenValid(tokenCreation.toLong(), tokenExpiresInSeconds.toLong())) {
                requestTokenRefresh()
            }
            fetchUserProfile()
        } else {
            setUser(null)
        }
    }

    fun getLastUsedCredentials(): SurveyCredentials = SurveyCredentials(lastEmail, lastPassword)

    // auxiliary functions

    private fun validCredentials(email: String, password: String): Boolean {
        return if (email.isEmpty()) {
            setMessage("Please enter your email")
            false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            setMessage("Please enter a valid email")
            false
        } else if (password.isEmpty()) {
            setMessage("Please enter your password")
            false
        } else {
            true
        }
    }

    private fun updatePreferences(token: Token) {
        // update preferences
        accessToken = token.accessToken
        refreshToken = token.refreshToken
        tokenCreation = token.createdAt.toString()
        tokenExpiresInSeconds = token.expiresIn.toString()
    }

    private fun loadUserProfile(userProfile: User) {
        val email = userProfile.email
        val name = userProfile.name
        val avatarUrl = userProfile.avatarUrl

        if (email.isEmpty() or name.isEmpty()) {
            throw IllegalStateException("Invalid user profile")
        } else {
            val user = User(email = email, name = name, avatarUrl = avatarUrl)
            setUser(user)
        }
    }

    companion object {
        private const val TAG = "LoginViewModel"
    }

}

class LoginViewModelFactory(
    private val application: Application,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            val userRepository = UserRepository(RetrofitService.nimbleSurveyApi)

            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(application, userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}