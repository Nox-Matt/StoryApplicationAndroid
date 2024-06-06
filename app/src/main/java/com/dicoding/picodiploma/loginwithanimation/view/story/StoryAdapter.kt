package com.dicoding.picodiploma.loginwithanimation.view.story

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.response.ListStoryItem

class StoryAdapter(private val listener: OnStoryItemClickListener) : RecyclerView.Adapter<StoryAdapter.ViewHolder>() {

    private var storyItems: List<ListStoryItem> = emptyList()

    interface OnStoryItemClickListener {
        fun onStoryItemClick(storyId: String, sharedViews: List<Pair<View, String>>)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rv_story_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = storyItems[position]
        holder.bind(item)
        holder.itemView.setOnClickListener {
            item.id?.let { id ->
                val sharedViews = listOf(
                    Pair(holder.imgStory as View, "detailPhoto"),
                    Pair(holder.titleStory as View, "title"),
                    Pair(holder.titleDesc as View, "description")
                )
                listener.onStoryItemClick(id, sharedViews)
            }
        }
    }

    override fun getItemCount(): Int = storyItems.size

    fun getStoriesList(newStories: List<ListStoryItem>) {
        storyItems = newStories
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgStory: ImageView = view.findViewById(R.id.story_pict)
        val titleStory: TextView = view.findViewById(R.id.story_name)
        val titleDesc: TextView = view.findViewById(R.id.story_desc)

        fun bind(item: ListStoryItem) {
            titleStory.text = item.name
            titleDesc.text = item.description
            Glide.with(itemView.context)
                .load(item.photoUrl)
                .into(imgStory)
        }
    }
}