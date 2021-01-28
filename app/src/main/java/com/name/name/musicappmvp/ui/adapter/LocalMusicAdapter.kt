package com.name.name.musicappmvp.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.name.name.musicappmvp.R
import com.name.name.musicappmvp.data.model.Song
import com.name.name.musicappmvp.ultis.DataManager
import kotlinx.android.synthetic.main.item_music.view.*

class LocalMusicAdapter(var onItemClick: () -> Unit) : RecyclerView.Adapter<ViewHolder>() {

    private var songs = listOf<Song>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.item_music, parent, false), onItemClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(songs[position], position)
    }

    override fun getItemCount(): Int = songs.size

    fun sendList(list: List<Song>) {
        songs = list
        notifyDataSetChanged()
    }
}

class ViewHolder(itemView: View, var onItemClick: () -> Unit) : RecyclerView.ViewHolder(itemView) {
    private val songView: TextView = itemView.song

    fun bind(song: Song, position: Int) {
        songView.text = song.title
        songView.setOnClickListener {
            DataManager.position = position
            onItemClick()
        }
    }
}
