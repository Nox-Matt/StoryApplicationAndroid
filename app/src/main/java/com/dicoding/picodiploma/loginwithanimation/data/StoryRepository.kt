package com.dicoding.picodiploma.loginwithanimation.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.dicoding.picodiploma.loginwithanimation.api.ApiService
import com.dicoding.picodiploma.loginwithanimation.response.AddStoryResponse
import com.dicoding.picodiploma.loginwithanimation.response.DetailResponse
import com.dicoding.picodiploma.loginwithanimation.response.ListStoryItem
import com.dicoding.picodiploma.loginwithanimation.response.StoryResponse
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody

class StoryRepository(private val apiService: ApiService) {

    suspend fun getStories(page: Int): StoryResponse {
        return apiService.getStories(page = page)
    }

    suspend fun getStoryDetail(id: String): DetailResponse {
        return apiService.getStoryDetail(id)
    }

    suspend fun addNewStory(photo: MultipartBody.Part, description: RequestBody): AddStoryResponse {
        return apiService.addNewStory(photo, description)
    }

    suspend fun getStoriesWithLocation(): StoryResponse {
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