package com.name.name.musicappmvp.data.repository

import com.name.name.musicappmvp.data.source.LocalSongDataSource
import com.name.name.musicappmvp.data.source.OnGotListCallback

class LocalSongRepository(private val localSongDataSource: LocalSongDataSource.Local){

    fun getLocalSongs(callback: OnGotListCallback){
        localSongDataSource.getLocalSong(callback)
    }

    companion object {
        private var instance: LocalSongRepository? = null

        fun getInstanceRepository(localSongDataSource: LocalSongDataSource.Local) =
            instance ?: LocalSongRepository(localSongDataSource).also { instance = it }
    }
}

