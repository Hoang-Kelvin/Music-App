package com.name.name.musicappmvp.data.source

import com.name.name.musicappmvp.data.model.LocalSong

interface LocalSongDataSource {
    interface Local {
        fun getLocalSong(callback: OnGotListCallback): MutableList<LocalSong>
    }
}
