package com.name.name.musicappmvp.data.repository

import android.content.Context
import com.name.name.musicappmvp.data.model.LocalSong
import com.name.name.musicappmvp.data.source.SongDataSource
import com.name.name.musicappmvp.data.source.local.LocalSource

class SongRepository: SongDataSource.Local {
    override fun getLocalSong(context: Context): MutableList<LocalSong> {
        val localSource = LocalSource()
        return localSource.getLocalSong(context)
    }
}