package com.vidixmx.nimblesurveys.ui.surveys

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.text.HtmlCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.vidixmx.nimblesurveys.data.model.Survey
import com.vidixmx.nimblesurveys.data.model.User
import com.vidixmx.nimblesurveys.databinding.ActivitySurveyDetailsBinding
import com.vidixmx.nimblesurveys.ui.common.toast

class SurveyDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySurveyDetailsBinding

    private lateinit var viewModel: SurveyDetailsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Instantiate ViewModel
        val factory = SurveyDetailsViewModelFactory(application)
        viewModel = ViewModelProvider(this, factory)[SurveyDetailsViewModel::class.java]

        // Instantiate binding
        binding = ActivitySurveyDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

    override fun onStart() {
        super.onStart()

        setupActivity()
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.message.observe(this) { message ->
            toast(message)
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.surveyDetails.observe(this) { survey ->
            survey?.let { details ->
                refreshDetails(details)
            }
        }
    }

    private fun refreshDetails(details: Survey) {
        with(binding) {
            txtSurveyTitle.text = details.title
            txtSurveyDescription.text = details.description
            txtThanks.text = HtmlCompat.fromHtml(
                details.thankEmailAboveThreshold.replace(
                    "{name}", "Customer"
                ), HtmlCompat.FROM_HTML_MODE_COMPACT
            )

            if (details.coverImageUrl.isNotEmpty()) {
                Glide.with(this@SurveyDetailsActivity)
                    .load(details.coverImageUrl)
                    .fitCenter()
                    .into(imgDetailsCover)
            }
        }
        viewModel.setIsLoading(false)

    }

    private fun setupActivity() {
        val surveyId = intent.extras?.getString(SURVEY_ID_ARGUMENT) ?: ""
        if (surveyId.isNotEmpty()) {
            viewModel.getSurveyDetails(surveyId)
        }
    }

    companion object {
        private const val SURVEY_ID_ARGUMENT = "survey_id"
        fun show(packageContext: AppCompatActivity, surveyId: String) {
            val intent = Intent(packageContext, SurveyDetailsActivity::class.java).apply {
                putExtra(SURVEY_ID_ARGUMENT, surveyId)
            }
            packageContext.startActivity(intent)
        }

    }
}