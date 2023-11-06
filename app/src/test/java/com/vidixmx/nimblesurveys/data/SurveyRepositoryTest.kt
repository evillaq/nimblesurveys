package com.vidixmx.nimblesurveys.data

import com.google.gson.Gson
import com.vidixmx.nimblesurveys.data.remote.NimbleSurveyApi
import com.vidixmx.nimblesurveys.data.remote.SurveyDetailsResponse
import com.vidixmx.nimblesurveys.data.remote.SurveysResponse
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import retrofit2.Response

class SurveyRepositoryTest {

    private lateinit var repository: SurveyRepository
    private val api = Mockito.mock(NimbleSurveyApi::class.java)

    @Before
    fun setUp() {
        repository = SurveyRepository(api)
    }

    @Test
    fun `getSurveys should return expected response`() = runBlocking {
        val pageNumber = 1
        val pageSize = 3
        val token = "123-f2i0CG6MDsf-wJE9FyYrhSGAOtxBkhYWDI"
        val surveysResponse = getMockSurveyResponse()

        // Mock the API response
        `when`(api.getSurveys("Bearer $token", mapOf("number" to pageNumber, "size" to pageSize)))
            .thenReturn(Response.success(surveysResponse))

        // Call the repository function
        val response = repository.getSurveys(token, pageNumber, pageSize)

        // Assert the result
        assert(response.isSuccessful)
        assert(response.body() == surveysResponse)
    }

    @Test
    fun `getSurveyDetails should return expected response`() = runBlocking {
        val token = "your_token"
        val surveyId = "your_survey_id"
        val surveyDetailsResponse = getMockSurveyDetailsResponse()

        // Mock the API response
        `when`(api.getSurveyDetails("Bearer $token", surveyId))
            .thenReturn(Response.success(surveyDetailsResponse))

        // Call the repository function
        val response = repository.getSurveyDetails(token, surveyId)

        // Assert the result
        assert(response.isSuccessful)
        assert(response.body() == surveyDetailsResponse)
    }

    // auxiliary functions
    private fun getMockSurveyResponse(): SurveysResponse {
        val jsonString =
            """{
    "data": [
        {
            "id": "d5de6a8f8f5f1cfe51bc",
            "type": "survey_simple",
            "attributes": {
                "title": "Scarlett Bangkok",
                "description": "We'd love ot hear from you!",
                "thank_email_above_threshold": "<span style=\"font-family:arial,helvetica,sans-serif\"><span style=\"font-size:14px\">Dear {name},<br /><br />Thank you for visiting Scarlett Wine Bar &amp; Restaurant at Pullman Bangkok Hotel G &nbsp;and for taking the time to complete our guest feedback survey!<br /><br />Your feedback is very important to us and each survey is read individually by the management and owners shortly after it is sent. We discuss comments and suggestions at our daily meetings and use them to constantly improve our services.<br /><br />We would very much appreciate it if you could take a few more moments and review us on TripAdvisor regarding your recent visit. By <a href=\"https://www.tripadvisor.com/Restaurant_Review-g293916-d2629404-Reviews-Scarlett_Wine_Bar_Restaurant-Bangkok.html\">clicking here</a> you will be directed to our page.&nbsp;<br /><br />Thank you once again and we look forward to seeing you soon!<br /><br />The Team at Scarlett Wine Bar &amp; Restaurant&nbsp;</span></span><span style=\"font-family:arial,helvetica,sans-serif; font-size:14px\">Pullman Bangkok Hotel G</span>",
                "thank_email_below_threshold": "<span style=\"font-size:14px\"><span style=\"font-family:arial,helvetica,sans-serif\">Dear {name},<br /><br />Thank you for visiting&nbsp;</span></span><span style=\"font-family:arial,helvetica,sans-serif; font-size:14px\">Uno Mas at Centara Central World&nbsp;</span><span style=\"font-size:14px\"><span style=\"font-family:arial,helvetica,sans-serif\">&nbsp;and for taking the time to complete our customer&nbsp;feedback survey.</span></span><br /><br /><span style=\"font-family:arial,helvetica,sans-serif; font-size:14px\">The Team at&nbsp;</span><span style=\"font-family:arial,helvetica,sans-serif\"><span style=\"font-size:14px\">Scarlett Wine Bar &amp; Restaurant&nbsp;</span></span><span style=\"font-family:arial,helvetica,sans-serif; font-size:14px\">Pullman Bangkok Hotel G</span>",
                "is_active": true,
                "cover_image_url": "https://dhdbhh0jsld0o.cloudfront.net/m/1ea51560991bcb7d00d0_",
                "created_at": "2017-01-23T07:48:12.991Z",
                "active_at": "2015-10-08T07:04:00.000Z",
                "inactive_at": null,
                "survey_type": "Restaurant"
            }
        }
    ],
    "meta": {
        "page": 1,
        "pages": 20,
        "page_size": 1,
        "records": 20
    }
}"""
        return Gson().fromJson(jsonString, SurveysResponse::class.java)

    }

    private fun getMockSurveyDetailsResponse(): SurveyDetailsResponse {
        val jsonString =
            """{
    "data": {
        "id": "d5de6a8f8f5f1cfe51bc",
        "type": "survey",
        "attributes": {
            "title": "Scarlett Bangkok",
            "description": "We'd love ot hear from you!",
            "thank_email_above_threshold": "<span style=\"font-family:arial,helvetica,sans-serif\"><span style=\"font-size:14px\">Dear {name},<br /><br />Thank you for visiting Scarlett Wine Bar &amp; Restaurant at Pullman Bangkok Hotel G &nbsp;and for taking the time to complete our guest feedback survey!<br /><br />Your feedback is very important to us and each survey is read individually by the management and owners shortly after it is sent. We discuss comments and suggestions at our daily meetings and use them to constantly improve our services.<br /><br />We would very much appreciate it if you could take a few more moments and review us on TripAdvisor regarding your recent visit. By <a href=\"https://www.tripadvisor.com/Restaurant_Review-g293916-d2629404-Reviews-Scarlett_Wine_Bar_Restaurant-Bangkok.html\">clicking here</a> you will be directed to our page.&nbsp;<br /><br />Thank you once again and we look forward to seeing you soon!<br /><br />The Team at Scarlett Wine Bar &amp; Restaurant&nbsp;</span></span><span style=\"font-family:arial,helvetica,sans-serif; font-size:14px\">Pullman Bangkok Hotel G</span>",
            "thank_email_below_threshold": "<span style=\"font-size:14px\"><span style=\"font-family:arial,helvetica,sans-serif\">Dear {name},<br /><br />Thank you for visiting&nbsp;</span></span><span style=\"font-family:arial,helvetica,sans-serif; font-size:14px\">Uno Mas at Centara Central World&nbsp;</span><span style=\"font-size:14px\"><span style=\"font-family:arial,helvetica,sans-serif\">&nbsp;and for taking the time to complete our customer&nbsp;feedback survey.</span></span><br /><br /><span style=\"font-family:arial,helvetica,sans-serif; font-size:14px\">The Team at&nbsp;</span><span style=\"font-family:arial,helvetica,sans-serif\"><span style=\"font-size:14px\">Scarlett Wine Bar &amp; Restaurant&nbsp;</span></span><span style=\"font-family:arial,helvetica,sans-serif; font-size:14px\">Pullman Bangkok Hotel G</span>",
            "is_active": true,
            "cover_image_url": "https://dhdbhh0jsld0o.cloudfront.net/m/1ea51560991bcb7d00d0_",
            "created_at": "2017-01-23T07:48:12.991Z",
            "active_at": "2015-10-08T07:04:00.000Z",
            "inactive_at": null,
            "survey_type": "Restaurant"
        },
        "relationships": {
            "questions": {
                "data": [
                    {
                        "id": "940d229e4cd87cd1e202",
                        "type": "question"
                    }
                ]
            }
        }
    },
    "included": [
        {
            "id": "4cbc3e5a1c87d99bc7ee",
            "type": "answer",
            "attributes": {
                "text": "1",
                "help_text": null,
                "input_mask_placeholder": null,
                "short_text": "answer_1",
                "is_mandatory": false,
                "is_customer_first_name": false,
                "is_customer_last_name": false,
                "is_customer_title": false,
                "is_customer_email": false,
                "prompt_custom_answer": false,
                "weight": null,
                "display_order": 0,
                "display_type": "default",
                "input_mask": null,
                "date_constraint": null,
                "default_value": null,
                "response_class": "answer",
                "reference_identifier": null,
                "score": 0,
                "alerts": []
            }
        },
        {
            "id": "940d229e4cd87cd1e202",
            "type": "question",
            "attributes": {
                "text": "Food & Variety, Taste and Presentation",
                "help_text": null,
                "display_order": 1,
                "short_text": "Food",
                "pick": "one",
                "display_type": "star",
                "is_mandatory": false,
                "correct_answer_id": null,
                "facebook_profile": null,
                "twitter_profile": null,
                "image_url": null,
                "cover_image_url": "https://dhdbhh0jsld0o.cloudfront.net/m/b41c84934fa8e4c34269_",
                "cover_image_opacity": 0.75,
                "cover_background_color": null,
                "is_shareable_on_facebook": false,
                "is_shareable_on_twitter": false,
                "font_face": null,
                "font_size": null,
                "tag_list": ""
            },
            "relationships": {
                "answers": {
                    "data": [
                        {
                            "id": "4cbc3e5a1c87d99bc7ee",
                            "type": "answer"
                        }
                    ]
                }
            }
        }
    ]
}"""

        return Gson().fromJson(jsonString, SurveyDetailsResponse::class.java)
    }


}