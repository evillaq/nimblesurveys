package com.vidixmx.nimblesurveys.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.vidixmx.nimblesurveys.data.UserRepository
import com.vidixmx.nimblesurveys.data.model.User
import com.vidixmx.nimblesurveys.data.remote.RetrofitService
import com.vidixmx.nimblesurveys.databinding.ActivityLoginBinding
import com.vidixmx.nimblesurveys.ui.common.toast

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Instantiate viewModel
        val repository = UserRepository(RetrofitService.nimbleSurveyApi)
        val factory = LoginViewModelFactory(application, repository)
        viewModel = ViewModelProvider(this, factory)[LoginViewModel::class.java]

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

    override fun onStart() {
        super.onStart()

        setupActivity()
        observeViewModel()

        // check if user is already logged in
        viewModel.validateToken()
    }

    private fun setControlsForTest() {
        val testUser = User("evillaq@hotmail.com", "EVQ", "12345678")
        with(binding) {
            etEmail.setText(testUser.email)
            etPassword.setText(testUser.password)
        }
    }

    private fun setupActivity() {

        // set controls for test: TEMP!!
        setControlsForTest()

        with(binding) {
            btnLogin.setOnClickListener {
                val email = etEmail.text.toString().trim()
                val password = etPassword.text.toString().trim()
                viewModel.loginUser(email, password)
            }
        }

    }

    private fun observeViewModel() {

        viewModel.message.observe(this) { message ->
            toast(message)
        }

        viewModel.user.observe(this) { user ->
            user?.let {
                HomeScreenActivity.show(this@LoginActivity, user)
            }
        }

    }

}

