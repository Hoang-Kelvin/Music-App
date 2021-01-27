package com.name.name.musicappmvp.ui.main

import android.content.Context
import com.name.name.musicappmvp.data.model.LocalSong

class LocalMusicInterface {
    interface View{
        fun displayLocalSong()
        fun handleButton()
    }
    interface Presenter{
        fun getLocalSong(context: Context) : List<LocalSong>
        fun handlePlaySong()
    }
}