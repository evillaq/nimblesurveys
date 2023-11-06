package com.vidixmx.nimblesurveys.ui.surveys

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.vidixmx.nimblesurveys.data.model.Survey
import com.vidixmx.nimblesurveys.databinding.FragmentSurveyBinding

class SurveyFragment : Fragment() {

    private lateinit var binding: FragmentSurveyBinding
    private var survey: Survey? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { bundle ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                survey = bundle.getParcelable(SURVEY, Survey::class.java)
            } else {
                @Suppress("DEPRECATION")
                survey = bundle.getParcelable(SURVEY)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentSurveyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()

        setupFragmentControls()
        loadSurvey()
    }

    private fun setupFragmentControls() {
        with(binding) {
            fabTakeSurvey.setOnClickListener {
                survey?.let { survey ->
                    SurveyDetailsActivity.show(
                        requireActivity() as AppCompatActivity,
                        survey.id
                    )
                }
            }
        }
    }

    private fun loadSurvey() {
        survey?.let { surveyData ->
            with(binding) {
                txtSurveyTitle.text = surveyData.title
                txtSurveyDetails.text = surveyData.description
                // poster
                if (surveyData.coverImageUrl.isNotEmpty()) {
                    Glide.with(this@SurveyFragment)
                        .load(surveyData.coverImageUrl)
                        .fitCenter()
                        .into(imgCover)
                }
            }
        }
    }

    companion object {
        private const val SURVEY = "SURVEY"

        @JvmStatic
        fun newInstance(survey: Survey) =
            SurveyFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(SURVEY, survey)
                }
            }
    }

}