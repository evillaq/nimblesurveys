package com.vidixmx.nimblesurveys.ui.surveys

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.vidixmx.nimblesurveys.Constants.Preferences
import com.vidixmx.nimblesurveys.data.SurveyRepository
import com.vidixmx.nimblesurveys.data.model.Survey
import com.vidixmx.nimblesurveys.data.model.User
import com.vidixmx.nimblesurveys.data.remote.NimbleError
import com.vidixmx.nimblesurveys.utils.sharedPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SurveysViewModel(
    application: Application,
    private val repository: SurveyRepository,
) : AndroidViewModel(application) {

    // preferences delegation
    private var accessToken by sharedPreferences(Preferences.ACCESS_TOKEN, "")
    private var refreshToken by sharedPreferences(Preferences.REFRESH_TOKEN, "")
    private var tokenCreation by sharedPreferences(Preferences.TOKEN_CREATION_TIMESTAMP, "")
    private var tokenExpiresInSeconds by sharedPreferences(Preferences.TOKEN_EXPIRES_IN_SECONDS, "")

    private val dateFormatter = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault())

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    private fun setIsLoading(newValue: Boolean) {
        viewModelScope.launch {
            _isLoading.postValue(newValue)
        }
    }

    private val _user: MutableLiveData<User?> = MutableLiveData()
    val user: LiveData<User?> = _user

    fun setCurrentUser(user: User?) {
        _user.value = user
    }

    private val _surveys = MutableLiveData<List<Survey>>()
    val surveys: LiveData<List<Survey>> = _surveys

    private fun setSurveys(surveys: List<Survey>) {
        viewModelScope.launch {
            _surveys.postValue(surveys)
        }
    }

    fun fetchSurveys() {
        setIsLoading(true)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repository.getSurveys(accessToken)
                if (response.isSuccessful) {

                    println("Surveys: ${response.body().toString()}")

                    response.body()?.let {
                        val surveys = it.toSurveyList()
                        if (surveys.isNotEmpty()) {
                            setSurveys(surveys)
                        } else {
                            setMessage("No surveys found")
                        }
                    }
                } else {
                    val responseError: NimbleError? =
                        NimbleError.fromString(response.errorBody()!!.string())
                    responseError?.let { error ->
                        if (error.errors.isNotEmpty()) {
                            _message.postValue(error.errors[0].detail)
                        }
                    }
                }
            } catch (e: Exception) {
                setMessage(e.localizedMessage ?: "Error fetching surveys")
            } finally {
                setIsLoading(false)
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
    private val repository: SurveyRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SurveysViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SurveysViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
