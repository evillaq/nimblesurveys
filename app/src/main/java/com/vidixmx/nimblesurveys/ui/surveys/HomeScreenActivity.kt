package com.vidixmx.nimblesurveys.ui.surveys

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator
import com.vidixmx.nimblesurveys.R
import com.vidixmx.nimblesurveys.data.model.User
import com.vidixmx.nimblesurveys.databinding.ActivityHomeScreenBinding
import com.vidixmx.nimblesurveys.databinding.DrawerHeaderBinding

class HomeScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeScreenBinding
    private lateinit var headerBinding: DrawerHeaderBinding

    private lateinit var viewModel: SurveysViewModel

    private lateinit var pageAdapter: SurveysPageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Instantiate viewModel
        val factory = SurveysViewModelFactory(application)
        viewModel = ViewModelProvider(this, factory)[SurveysViewModel::class.java]

        // Instantiate bindings
        binding = ActivityHomeScreenBinding.inflate(layoutInflater)
        headerBinding = DrawerHeaderBinding.bind(binding.navView.getHeaderView(0))
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

        setNavigationViewListener()

        // setup backpressed handler to end application
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                moveTaskToBack(true)
                finish()
            }
        })

        setupButtons()
        setSurveyNavigation()
        observeViewModel()

        viewModel.fetchSurveys()
    }

    private fun setupButtons() {
        with(binding.mainContent) {
            imgAvatar.setOnClickListener {
                binding.drawerLayout.openDrawer(GravityCompat.END)
            }
        }
    }

    private fun setSurveyNavigation() {
        pageAdapter = SurveysPageAdapter(supportFragmentManager, lifecycle)

        with(binding.mainContent) {
            viewPager.adapter = pageAdapter
            TabLayoutMediator(
                tabLayout,
                viewPager
            ) { _, _ -> }.attach()
        }

    }

    private fun setNavigationViewListener() {
        binding.navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_logout -> {
                    viewModel.logout()
                    true
                }
                else -> false
            }
        }
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

        viewModel.userLoggedOut.observe(this) { loggedOut ->
            if (loggedOut) {
                finish()
            }
        }
    }

    private fun refreshScreenControls(user: User) {
        with(binding.mainContent) {
            txtDate.text = viewModel.getCurrentDate()
            txtHeader.text = getString(R.string.today_label)
            if (user.avatarUrl != "") {
                Glide.with(this@HomeScreenActivity)
                    .load(user.avatarUrl)
                    .circleCrop()
                    .into(imgAvatar)
            }

            with(headerBinding) {
                Glide.with(this@HomeScreenActivity)
                    .load(user.avatarUrl)
                    .circleCrop()
                    .into(imgAvatarDrawerHeader)
                txtUserName.text = user.name
            }
        }
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
