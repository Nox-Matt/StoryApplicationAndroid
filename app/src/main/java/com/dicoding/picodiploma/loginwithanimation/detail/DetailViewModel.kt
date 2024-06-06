package com.dicoding.picodiploma.loginwithanimation.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.picodiploma.loginwithanimation.data.StoryRepository
import com.dicoding.picodiploma.loginwithanimation.response.StoryDetail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DetailViewModel(private val repository: StoryRepository) : ViewModel(){

    private val _storyDetail = MutableLiveData<StoryDetail>().apply { value = StoryDetail() }
    val storyDetail: LiveData<StoryDetail>
        get() = _storyDetail

    private val _error = MutableLiveData<String?>()
    val error: MutableLiveData<String?>
        get() = _error

    fun fetchStoryDetail(storyId: String) {
        viewModelScope.launch(Dispatchers.Main) {
            try {
                val response = repository.getStoryDetail(storyId)
                if (response.error == false) {
                    _storyDetail.value = response.storyDetail
                } else {
                    _error.value = response.message
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

}
