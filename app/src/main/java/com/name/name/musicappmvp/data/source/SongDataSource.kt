package com.name.name.musicappmvp.data.source

import android.content.Context
import com.name.name.musicappmvp.data.model.LocalSong

interface SongDataSource {
    interface Local{
        fun getLocalSong(context: Context) : MutableList<LocalSong>
    }
}