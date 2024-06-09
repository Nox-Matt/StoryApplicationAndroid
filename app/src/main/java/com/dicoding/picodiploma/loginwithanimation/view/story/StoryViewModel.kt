package com.dicoding.picodiploma.loginwithanimation.view.story

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.dicoding.picodiploma.loginwithanimation.data.StoryRepository
import com.dicoding.picodiploma.loginwithanimation.response.ListStoryItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch



class StoryViewModel(private val repository: StoryRepository) : ViewModel() {

    private val _stories = MutableStateFlow<PagingData<ListStoryItem>>(PagingData.empty())
    val stories: StateFlow<PagingData<ListStoryItem>> = _stories.asStateFlow()

    private val _storiesWithLocation = MutableStateFlow<List<ListStoryItem>>(emptyList())
    val storiesWithLocation: StateFlow<List<ListStoryItem>> = _storiesWithLocation.asStateFlow()


    init {
        fetchStories()
    }

    fun fetchStories() {
        getAllStories()
    }

    fun fetchStoriesWithLocation() {
        getAllStoriesWithLocation()
    }

    private fun getAllStories() {
        viewModelScope.launch {
            try {
                repository.getFlowStories()
                    .collect { pagingData ->
                        _stories.value = pagingData
                    }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to fetch stories", e)
            }
        }
    }

    private fun getAllStoriesWithLocation() {
        viewModelScope.launch {
            try {
                val response = repository.getStoriesWithLocation()
                val listStory = response.listStory?.filterNotNull() ?: emptyList()
                _storiesWithLocation.value = listStory
            } catch (e: Exception) {
                Log.e(TAG, "Failed to fetch stories with location", e)
            }
        }
    }

    companion object {
        private const val TAG = "StoryViewModel"
    }
}




