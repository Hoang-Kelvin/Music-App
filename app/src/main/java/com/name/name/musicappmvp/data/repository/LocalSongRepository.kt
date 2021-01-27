package com.name.name.musicappmvp.data.repository

import com.name.name.musicappmvp.data.model.LocalSong
import com.name.name.musicappmvp.data.source.LocalSongDataSource
import com.name.name.musicappmvp.data.source.OnGotListCallback
import java.lang.Exception

class LocalSongRepository(private val localSongDataSource: LocalSongDataSource.Local) :
    LocalSongDataSource.Local {

    override fun getLocalSong(callback: OnGotListCallback): MutableList<LocalSong> {
        try {
            callback.onSuccess(localSongDataSource.getLocalSong(callback))
        } catch (e: Exception) {
            callback.onFailure(e)
        }
        return localSongDataSource.getLocalSong(callback)
    }

    companion object {
        private var instance: LocalSongRepository? = null

        fun getInstanceRepository(localSongDataSource: LocalSongDataSource.Local) =
            instance ?: LocalSongRepository(localSongDataSource).also { instance = it }
    }
}
