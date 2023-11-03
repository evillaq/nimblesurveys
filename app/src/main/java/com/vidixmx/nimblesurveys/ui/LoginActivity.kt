package com.vidixmx.nimblesurveys.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.vidixmx.nimblesurveys.data.UserRepository
import com.vidixmx.nimblesurveys.data.model.User
import com.vidixmx.nimblesurveys.data.remote.RetrofitService
import com.vidixmx.nimblesurveys.databinding.ActivityLoginBinding
import retrofit2.Retrofit

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Instantiate viewModel
        val repository = UserRepository(RetrofitService.nimbleSurveyApi)
        val factory = LoginViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[LoginViewModel::class.java]

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

    override fun onStart() {
        super.onStart()

        setupActivity()

        // temp
        val testUser = User("evillaq@hotmail.com", "EVQ", "12345678")
        viewModel.loginUser(testUser.email, testUser.password)
    }

    private fun setupActivity() {

        with(binding) {
            btnLogin.setOnClickListener {

            }
        }

    }


}

