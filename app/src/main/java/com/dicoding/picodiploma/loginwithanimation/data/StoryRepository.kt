package com.dicoding.picodiploma.loginwithanimation.data

import com.dicoding.picodiploma.loginwithanimation.api.ApiService
import com.dicoding.picodiploma.loginwithanimation.response.AddStoryResponse
import com.dicoding.picodiploma.loginwithanimation.response.DetailResponse
import com.dicoding.picodiploma.loginwithanimation.response.StoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody

class StoryRepository(private val apiService: ApiService) {

    suspend fun getStories(): StoryResponse {
        return apiService.getStories()
    }
    suspend fun getStoryDetail(id: String): DetailResponse {
        return apiService.getStoryDetail(id)
    }
    suspend fun addNewStory(photo: MultipartBody.Part, description: RequestBody): AddStoryResponse {
        return apiService.addNewStory(photo, description)
    }
    suspend fun getStoriesWithLocation() : StoryResponse {
        return apiService.getStoriesWithLocation()
    }

    companion object {
        @Volatile
        private var INSTANCE: StoryRepository? = null

        fun getInstance(apiService: ApiService): StoryRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: StoryRepository(apiService).also { INSTANCE = it }
            }
        }
    }
}