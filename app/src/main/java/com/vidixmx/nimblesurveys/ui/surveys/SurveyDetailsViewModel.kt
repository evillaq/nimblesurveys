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
import com.vidixmx.nimblesurveys.data.remote.NimbleError
import com.vidixmx.nimblesurveys.data.remote.RetrofitService
import com.vidixmx.nimblesurveys.utils.sharedPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SurveyDetailsViewModel(
    application: Application,
    private val repository: SurveyRepository,
) : AndroidViewModel(application) {

    // preferences delegation
    private val token by sharedPreferences(Preferences.ACCESS_TOKEN, "")

    private val _surveyDetails = MutableLiveData<Survey>()
    val surveyDetails: LiveData<Survey> = _surveyDetails

    private fun setSurveyDetails(survey: Survey) {
        viewModelScope.launch {
            _surveyDetails.value = survey
        }
    }

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    fun setIsLoading(newValue: Boolean) {
        viewModelScope.launch {
            _isLoading.postValue(newValue)
        }
    }

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message
    private fun setMessage(message: String) {
        viewModelScope.launch {
            _message.postValue(message)
        }
    }

    fun getSurveyDetails(surveyId: String) {

        setIsLoading(true)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repository.getSurveyDetails(token, surveyId)
                if (response.isSuccessful) {
                    response.body()?.let {
                        setSurveyDetails(it.data.survey)
                    }
                } else {
                    val responseError: NimbleError? =
                        NimbleError.fromString(response.errorBody()!!.string())
                    responseError?.let { error ->
                        if (error.errors.isNotEmpty()) {
                            setMessage(error.errors[0].detail)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                setMessage("Error logging user out")
            }
        }

    }

}

class SurveyDetailsViewModelFactory(
    private val application: Application,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SurveyDetailsViewModel::class.java)) {
            val repository = SurveyRepository(RetrofitService.nimbleSurveyApi)
            @Suppress("UNCHECKED_CAST")
            return SurveyDetailsViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
