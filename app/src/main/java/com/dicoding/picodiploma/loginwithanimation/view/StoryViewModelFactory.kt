package com.dicoding.picodiploma.loginwithanimation.view

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.picodiploma.loginwithanimation.data.StoryRepository
import com.dicoding.picodiploma.loginwithanimation.detail.DetailViewModel
import com.dicoding.picodiploma.loginwithanimation.di.Injection
import com.dicoding.picodiploma.loginwithanimation.view.story.StoryViewModel
import com.dicoding.picodiploma.loginwithanimation.view.story.add.AddStoryViewModel
import kotlinx.coroutines.runBlocking


class StoryViewModelFactory(
    private val repository: StoryRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StoryViewModel(repository) as T
        } else if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DetailViewModel(repository) as T
        } else if (modelClass.isAssignableFrom(AddStoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddStoryViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

    companion object {
        @Volatile
        private var INSTANCE: StoryViewModelFactory? = null

        fun getInstance(context: Context): StoryViewModelFactory {
            return INSTANCE ?: synchronized(this) {
                val repository = runBlocking { Injection.provideStoryRepository(context) }
                INSTANCE ?: StoryViewModelFactory(repository).also { INSTANCE = it }
            }
        }
    }
}
