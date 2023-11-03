package com.vidixmx.nimblesurveys.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.vidixmx.nimblesurveys.data.UserRepository
import com.vidixmx.nimblesurveys.data.remote.NimbleError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginViewModel(
    private val repository: UserRepository,
) : ViewModel() {

    fun loginUser(email: String, password: String) {

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = repository.loginUser(email, password)
                withContext(Dispatchers.Main) {
                    if (result.isSuccessful) {
                        result.body()?.let { loginResponse ->

                            Log.i(TAG, "loginResponse.data: ${loginResponse.data}")

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

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    companion object {
        private const val TAG = "LoginViewModel"
    }

}

class LoginViewModelFactory(private val repository: UserRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}