package com.dicoding.picodiploma.loginwithanimation

import com.dicoding.picodiploma.loginwithanimation.response.ListStoryItem
import com.dicoding.picodiploma.loginwithanimation.response.StoryResponse

object DataDummy {

    fun generateDummyStoryResponse(): StoryResponse {
        val listStoryItems: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val id = "story-$i"
            val name = "Name $i"
            val description = "Description $i"
            val photoUrl = "https://story-api.dicoding.dev/images/stories/photos-$i.png"
            val createdAt = "2024-06-07T09:28:12.768Z"
            val lat: Double? = if (i % 2 == 0) null else -6.4237547 + i
            val lon: Double? = if (i % 2 == 0) null else 106.8353655 + i

            val storyItem = ListStoryItem(
                id = id,
                name = name,
                description = description,
                photoUrl = photoUrl,
                createdAt = createdAt,
                lat = lat,
                lon = lon
            )
            listStoryItems.add(storyItem)
        }

        return StoryResponse(
            error = false,
            message = "Stories fetched successfully",
            listStory = listStoryItems
        )
    }
}