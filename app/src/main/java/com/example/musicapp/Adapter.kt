package com.example.musicapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class Adapter(context: Context, listSong: ArrayList<Song>, mainActivity: MainActivity) :
    RecyclerView.Adapter<Adapter.ViewHolder>() {
    private var mSongs: ArrayList<Song> = ArrayList()
    private var mInflater: LayoutInflater
    private var mContext: Context? = null
    private var mainActivity: MainActivity? = null
    var currentPosition = 0

    init {
        mSongs = listSong
        mContext = context
        mInflater = LayoutInflater.from(context)
        this.mainActivity = mainActivity
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val songView: TextView = itemView.findViewById(R.id.song)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        mContext = parent.context
        val view = mInflater.inflate(R.layout.item_music, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentSong = mSongs[position]
        holder.songView.text = currentSong.title
        holder.itemView.setOnClickListener {
            this.currentPosition = position
            this.mainActivity?.displaySong()
        }
    }

    override fun getItemCount(): Int {
        return mSongs.size
    }

}
