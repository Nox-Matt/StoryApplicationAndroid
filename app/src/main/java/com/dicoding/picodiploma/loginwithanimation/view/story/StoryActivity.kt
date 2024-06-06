package com.dicoding.picodiploma.loginwithanimation.view.story


import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityStoryBinding
import com.dicoding.picodiploma.loginwithanimation.detail.DetailActivity
import com.dicoding.picodiploma.loginwithanimation.view.MapsActivity
import com.dicoding.picodiploma.loginwithanimation.view.StoryViewModelFactory
import com.dicoding.picodiploma.loginwithanimation.view.story.add.AddStoryActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

class StoryActivity : AppCompatActivity(), StoryAdapter.OnStoryItemClickListener {

    private lateinit var binding: ActivityStoryBinding
    private lateinit var storyAdapter: StoryAdapter
    private lateinit var viewModel: StoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupViewModel()
        setupRecyclerView()
        observeStories()

        binding.toMapButton.setOnClickListener{
            val intent = Intent(this,MapsActivity::class.java)
            startActivity(intent)
        }
        binding.addStoryButton.setOnClickListener {
            val intent = Intent(this, AddStoryActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onStoryItemClick(storyId: String, sharedViews: List<Pair<View, String>>) {
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra(DetailActivity.STORY_ID_EXTRA, storyId)

        val sharedElements = sharedViews.map { pair ->
            androidx.core.util.Pair(pair.first, pair.second)
        }.toTypedArray()

        val optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(this, *sharedElements)
        startActivity(intent, optionsCompat.toBundle())
    }


    private fun setupViewModel() {
        val factory = StoryViewModelFactory.getInstance(this)
        viewModel = ViewModelProvider(this, factory)[StoryViewModel::class.java]
        viewModel.getAllStories()
    }

    private fun setupRecyclerView() {
        storyAdapter = StoryAdapter(this)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@StoryActivity)
            adapter = storyAdapter
        }
    }

    private fun observeStories() {
        viewModel.stories.observe(this) { stories ->
            storyAdapter.getStoriesList(stories)
        }
    }
}