package com.dicoding.picodiploma.loginwithanimation.view.story

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.picodiploma.loginwithanimation.data.StoryRepository
import com.dicoding.picodiploma.loginwithanimation.response.ListStoryItem
import kotlinx.coroutines.launch


class StoryViewModel(private val repository: StoryRepository) : ViewModel() {

    private val _stories = MutableLiveData<List<ListStoryItem>>()
    val stories: LiveData<List<ListStoryItem>>
        get() = _stories

    init {
        getAllStoriesWithLocation()
    }

    fun getAllStories() {
        viewModelScope.launch {
            try {
                val storyResponse = repository.getStories()
                _stories.value = storyResponse.listStory?.filterNotNull().orEmpty()
            } catch (e: Exception) {
                Log.e(TAG, "Failed to fetch stories", e)
            }
        }
    }
    private fun getAllStoriesWithLocation() {
        viewModelScope.launch {
            try {
                val storyResponse = repository.getStoriesWithLocation()
                _stories.value = storyResponse.listStory?.filterNotNull().orEmpty()
            } catch (e: Exception) {
                Log.e(TAG, "Failed to fetch stories", e)
            }
        }
    }

    companion object {
        private const val TAG = "StoryViewModel"
    }
}