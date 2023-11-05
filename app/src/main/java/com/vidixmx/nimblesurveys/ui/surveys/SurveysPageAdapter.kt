package com.vidixmx.nimblesurveys.ui.surveys

import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.vidixmx.nimblesurveys.data.model.Survey

class SurveysPageAdapter(
    fragmentManager: FragmentManager, lifecycle: Lifecycle,
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    private val surveysList = mutableListOf<Survey>()

    override fun getItemCount(): Int = surveysList.size

    override fun createFragment(position: Int): SurveyFragment {
        return SurveyFragment.newInstance(surveysList[position])
    }

    fun submitSurveys(surveys: List<Survey>) {
        surveysList.clear()
        surveysList.addAll(surveys)
    }

}