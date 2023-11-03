package com.vidixmx.nimblesurveys.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.vidixmx.nimblesurveys.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

    override fun onStart() {
        super.onStart()

        setupActivity()
    }

    private fun setupActivity() {

        with(binding) {
            btnLogin.setOnClickListener {

            }
        }

    }


}

