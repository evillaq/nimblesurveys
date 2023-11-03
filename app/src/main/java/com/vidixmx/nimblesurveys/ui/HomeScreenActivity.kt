package com.vidixmx.nimblesurveys.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import com.vidixmx.nimblesurveys.data.model.User
import com.vidixmx.nimblesurveys.databinding.ActivityHomeScreenBinding

class HomeScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

    companion object {

        private const val USER_ARGUMENT = "user"

        fun show(
            callingActivity: AppCompatActivity,
            userData: User,
        ) {
            val intent = Intent().apply {
                putExtras(
                    bundleOf(USER_ARGUMENT to userData)
                )
            }
            callingActivity.startActivity(intent)
        }

    }
}