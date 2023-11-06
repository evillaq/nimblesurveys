package com.vidixmx.nimblesurveys.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Survey(
    @SerializedName("active_at")
    val activeAt: String,
    @SerializedName("cover_image_url")
    val coverImageUrl: String,
    @SerializedName("created_at")
    val createdAt: String,
    val description: String,
    @SerializedName("inactive_at")
    val inactiveAt: String,
    @SerializedName("is_active")
    val isActive: Boolean,
    @SerializedName("survey_type")
    val surveyType: String,
    @SerializedName("thank_email_above_threshold")
    val thankEmailAboveThreshold: String,
    @SerializedName("thank_email_below_threshold")
    val thankEmailBelowThreshold: String,
    val title: String,
    var id: String,
) : Parcelable