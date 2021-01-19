package com.example.musicapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class Adapter(var listSong: ArrayList<Song>, private var mainActivity: MainActivity) :
    RecyclerView.Adapter<Adapter.ViewHolder>() {
    var currentPosition = 0

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val songView: TextView = itemView.findViewById(R.id.song)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.item_music, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentSong = listSong[position]
        holder.songView.text = currentSong.title
        holder.itemView.setOnClickListener {
            this.currentPosition = position
            this.mainActivity.displaySong()
        }
    }

    override fun getItemCount(): Int = listSong.size
}
