package com.vidixmx.nimblesurveys.ui.surveys

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator
import com.vidixmx.nimblesurveys.data.SurveyRepository
import com.vidixmx.nimblesurveys.data.model.User
import com.vidixmx.nimblesurveys.data.remote.RetrofitService
import com.vidixmx.nimblesurveys.databinding.ActivityHomeScreenBinding

class HomeScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeScreenBinding

    private lateinit var viewModel: SurveysViewModel

    private lateinit var pageAdapter: SurveysPageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Instantiate viewModel
        val repository = SurveyRepository(RetrofitService.nimbleSurveyApi)
        val factory = SurveysViewModelFactory(application, repository)
        viewModel = ViewModelProvider(this, factory)[SurveysViewModel::class.java]

        // Instantiate binding
        binding = ActivityHomeScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // recover user from intent
        viewModel.setCurrentUser(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(USER_ARGUMENT, User::class.java)
            } else {
                @Suppress("DEPRECATION")
                intent.extras?.getParcelable(USER_ARGUMENT)
            }
        )

    }

    private fun setupActivity() {
        setSurveyNavigation()
        observeViewModel()

        viewModel.fetchSurveys()
    }

    private fun setSurveyNavigation() {
        pageAdapter = SurveysPageAdapter(supportFragmentManager, lifecycle)

        binding.viewPager.adapter = pageAdapter
        TabLayoutMediator(
            binding.tabLayout,
            binding.viewPager
        ) { _, _ -> }.attach()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observeViewModel() {
        viewModel.user.observe(this) { user ->
            user?.let {
                refreshScreenControls(user)
            }
        }

        viewModel.surveys.observe(this) { surveys ->
            if (surveys.isNotEmpty()) {
                pageAdapter.submitSurveys(surveys)
                pageAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun refreshScreenControls(user: User) {
        with(binding) {
            txtDate.text = viewModel.getCurrentDate()
            txtHeader.text = "Hello!"

            if (user.avatarUrl != "") {
                Glide.with(this@HomeScreenActivity)
                    .load(user.avatarUrl)
                    .circleCrop()
                    .into(imgAvatar)
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onStart() {
        super.onStart()

        setupActivity()
    }

    companion object {

        private const val USER_ARGUMENT = "user"

        @JvmStatic
        fun show(
            packageContext: AppCompatActivity,
            userData: User,
        ) {
            val intent = Intent(packageContext, HomeScreenActivity::class.java).apply {
                putExtras(
                    bundleOf(USER_ARGUMENT to userData)
                )
            }
            packageContext.startActivity(intent)
        }

    }
}