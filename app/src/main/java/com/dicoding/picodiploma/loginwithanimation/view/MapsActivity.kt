package com.dicoding.picodiploma.loginwithanimation.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.dicoding.picodiploma.loginwithanimation.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityMapsBinding
import com.dicoding.picodiploma.loginwithanimation.response.ListStoryItem
import com.dicoding.picodiploma.loginwithanimation.view.story.StoryViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collectLatest

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var viewModel: StoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val factory = StoryViewModelFactory.getInstance(this)
        viewModel = ViewModelProvider(this, factory)[StoryViewModel::class.java]

        viewModel.fetchStoriesWithLocation()

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        setupMap()
        observeStoriesWithLocation()

        val initialCamera = LatLng(-2.5489, 118.0149)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initialCamera, 5f))
    }

    private fun setupMap() {
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true
    }

    private fun observeStoriesWithLocation() {
        lifecycleScope.launch {
            viewModel.storiesWithLocation.collectLatest { stories ->
                addStoryMarkers(stories)
            }
        }
    }

    private fun addStoryMarkers(stories: List<ListStoryItem>) {
        mMap.clear()
        for (story in stories) {
            if (story.lat != null && story.lon != null) {
                val latLng = LatLng(story.lat, story.lon)
                val marker = mMap.addMarker(
                    MarkerOptions()
                        .position(latLng)
                        .title(story.name)
                        .snippet(story.description)
                )
                marker?.tag = story.id
            }
        }
    }
}