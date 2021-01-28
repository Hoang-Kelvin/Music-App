package com.name.name.musicappmvp.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.name.name.musicappmvp.R
import com.name.name.musicappmvp.data.model.LocalSong
import com.name.name.musicappmvp.ultis.DataManager
import kotlinx.android.synthetic.main.item_music.view.*

class LocalMusicAdapter(var onItemClick: () -> Unit) : RecyclerView.Adapter<ViewHolder>() {

    private var listOfSong = listOf<LocalSong>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.item_music, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentSong = listOfSong[position]
        holder.initializeView(currentSong)
        holder.itemView.setOnClickListener {
            DataManager.position = position
            onItemClick()
        }
    }

    override fun getItemCount(): Int = listOfSong.size

    fun sendList(list: List<LocalSong>) {
        listOfSong = list
        notifyDataSetChanged()
    }
}

class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val songView: TextView = itemView.song
    fun initializeView(song: LocalSong) {
        songView.text = song.title
        itemView
    }
}
