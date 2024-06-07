package com.dicoding.picodiploma.loginwithanimation.view.story

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dicoding.picodiploma.loginwithanimation.data.StoryPagingSource
import com.dicoding.picodiploma.loginwithanimation.data.StoryRepository
import com.dicoding.picodiploma.loginwithanimation.response.ListStoryItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch



class StoryViewModel(private val repository: StoryRepository) : ViewModel() {

    private val _stories = MutableStateFlow<PagingData<ListStoryItem>>(PagingData.empty())
    val stories: StateFlow<PagingData<ListStoryItem>> = _stories

    private val _storiesWithLocation = MutableStateFlow<List<ListStoryItem>>(emptyList())
    val storiesWithLocation: StateFlow<List<ListStoryItem>> = _storiesWithLocation

    init {
        getAllStoriesWithLocation()
        getAllStories()
    }

    fun getAllStories() {
        viewModelScope.launch {
            try {
                val pager = Pager(
                    config = PagingConfig(pageSize = PAGE_SIZE),
                    pagingSourceFactory = { StoryPagingSource(repository) }
                )
                pager.flow.cachedIn(viewModelScope).collectLatest {
                    _stories.value = it
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
                _storiesWithLocation.value = (response.listStory ?: emptyList()) as List<ListStoryItem>
            } catch (e: Exception) {
                Log.e(TAG, "Failed to fetch stories with location", e)
            }
        }
    }

    companion object {
        private const val TAG = "StoryViewModel"
        private const val PAGE_SIZE = 20
    }
}