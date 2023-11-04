package com.vidixmx.nimblesurveys.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.vidixmx.nimblesurveys.Constants
import com.vidixmx.nimblesurveys.Constants.Preferences
import com.vidixmx.nimblesurveys.data.UserRepository
import com.vidixmx.nimblesurveys.data.model.User
import com.vidixmx.nimblesurveys.utils.sharedPreferences
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SurveysViewModel(
    application: Application,
    private val repository: UserRepository,
) : AndroidViewModel(application) {

    // preferences delegation
    private var accessToken by sharedPreferences(Preferences.ACCESS_TOKEN, "")
    private var refreshToken by sharedPreferences(Preferences.REFRESH_TOKEN, "")
    private var tokenCreation by sharedPreferences(Preferences.TOKEN_CREATION_TIMESTAMP, "")
    private var tokenExpiresInSeconds by sharedPreferences(Preferences.TOKEN_EXPIRES_IN_SECONDS, "")

    private val dateFormatter = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault())

    private val isLoading = MutableLiveData<Boolean>()
    private fun setIsLoading(newValue: Boolean) {
        viewModelScope.launch {
            isLoading.postValue(newValue)
        }
    }

    private val _user: MutableLiveData<User?> = MutableLiveData()
    val user: LiveData<User?> = _user

    fun setCurrentUser(user: User?) {
        _user.value = user
    }

    fun fetchSurveys() {
        viewModelScope.launch {
            try {

            } catch (e: Exception) {
                setMessage(e.localizedMessage ?: "Error fetching surveys")
            }
        }
    }

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    private fun setMessage(message: String) {
        viewModelScope.launch {
            _message.postValue(message)
        }
    }

    fun getCurrentDate(): String = dateFormatter.format(Date())

}

class SurveysViewModelFactory(
    private val application: Application,
    private val repository: UserRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SurveysViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SurveysViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
