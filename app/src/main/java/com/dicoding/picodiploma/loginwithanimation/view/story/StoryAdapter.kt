package com.dicoding.picodiploma.loginwithanimation.view.story

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.response.ListStoryItem

class StoryAdapter(private val listener: OnStoryItemClickListener) :
    PagingDataAdapter<ListStoryItem, StoryAdapter.ViewHolder>(StoryItemComparator) {

    interface OnStoryItemClickListener {
        fun onStoryItemClick(storyId: String, sharedViews: List<Pair<View, String>>)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rv_story_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        item?.let { holder.bind(it) }
        holder.itemView.setOnClickListener {
            item?.id?.let { id ->
                val sharedViews = listOf(
                    Pair(holder.imgStory as View, "detailPhoto"),
                    Pair(holder.titleStory as View, "title"),
                    Pair(holder.titleDesc as View, "description")
                )
                listener.onStoryItemClick(id, sharedViews)
            }
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
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

    companion object {
        private val StoryItemComparator = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}