package com.name.name.musicappmvp.data.source

interface LocalSongDataSource {
    interface Local {
        fun getLocalSong(callback: OnGotListCallback)
    }
}
