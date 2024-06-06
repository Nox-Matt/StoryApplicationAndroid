package com.dicoding.picodiploma.loginwithanimation.di

import android.content.Context
import com.dicoding.picodiploma.loginwithanimation.api.ApiConfig
import com.dicoding.picodiploma.loginwithanimation.data.StoryRepository
import com.dicoding.picodiploma.loginwithanimation.data.UserRepository
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserPreference
import com.dicoding.picodiploma.loginwithanimation.data.pref.dataStore
import kotlinx.coroutines.flow.first

object Injection {
    suspend fun provideRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val token = pref.getSession().first().token
        val apiService = ApiConfig.getApiService(token)
        return UserRepository.getInstance(pref, apiService)
    }

    suspend fun provideStoryRepository(context: Context): StoryRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val token = pref.getSession().first().token
        val apiService = ApiConfig.getApiService(token)
        return StoryRepository.getInstance(apiService)
    }
}
