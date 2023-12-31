package com.vidixmx.nimblesurveys.ui.login

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.vidixmx.nimblesurveys.databinding.ActivityLoginBinding
import com.vidixmx.nimblesurveys.ui.common.toast
import com.vidixmx.nimblesurveys.ui.surveys.HomeScreenActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Instantiate viewModel
        val factory = LoginViewModelFactory(application)
        viewModel = ViewModelProvider(this, factory)[LoginViewModel::class.java]

        // Instantiate binding
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

    private fun retrievedLastUsedCredentials() {
        val (email, password) = viewModel.getLastUsedCredentials()
        with(binding) {
            etEmail.setText(email)
            etPassword.setText(password)
        }
    }

    private fun setupActivity() {

        // retrieve last used credentials from preferences
        retrievedLastUsedCredentials()

        with(binding) {
            btnLogin.setOnClickListener {
                val email = etEmail.text.toString().trim()
                val password = etPassword.text.toString().trim()
                viewModel.loginUser(email, password)
            }
        }

    }

    private fun observeViewModel() {

        // Display messages
        viewModel.message.observe(this) { message ->
            toast(message)
        }

        // launch home screen when user is logged in
        viewModel.user.observe(this) { user ->
            user?.let {
                HomeScreenActivity.show(this@LoginActivity, user)
            }
        }

        // Progressbar visibility
        viewModel.isLoading.observe(this) { isVisible ->
            binding.progressBar.visibility = if (isVisible) View.VISIBLE else View.GONE
        }

    }

}

