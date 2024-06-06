package com.dicoding.picodiploma.loginwithanimation.detail

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityDetailBinding
import com.dicoding.picodiploma.loginwithanimation.response.StoryDetail
import com.dicoding.picodiploma.loginwithanimation.view.StoryViewModelFactory


class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private lateinit var viewModel: DetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val storyId = intent.getStringExtra(STORY_ID_EXTRA)
        val factory = StoryViewModelFactory.getInstance(this)
        viewModel = ViewModelProvider(this, factory)[DetailViewModel::class.java]

        storyId?.let {
            viewModel.fetchStoryDetail(it)
        }

        showLoadingIndicator()
        viewModel.storyDetail.observe(this) { storyDetail ->
            hideLoadingIndicator()
            storyDetail?.let {
                populateStoryDetail(it)
            }
        }

    }

    private fun showLoadingIndicator() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideLoadingIndicator() {
        binding.progressBar.visibility = View.GONE
    }

    private fun populateStoryDetail(storyDetail: StoryDetail) {
        binding.titleDetail.text = storyDetail.name
        binding.descDetail.text = storyDetail.description
        Glide.with(this)
            .load(storyDetail.photoUrl)
            .into(binding.detailPicture)
    }

    companion object {
        const val STORY_ID_EXTRA = "story_id_extra"
    }
}
