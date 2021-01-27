package com.name.name.musicappmvp.ui.main

import android.content.Context
import com.name.name.musicappmvp.data.model.LocalSong
import com.name.name.musicappmvp.data.repository.SongRepository

class LocalMusicPresenter(
    private val view: LocalMusicInterface.View,
    private val songRepository: SongRepository) :
    LocalMusicInterface.Presenter {
    override fun getLocalSong(context: Context): List<LocalSong> {
        return songRepository.getLocalSong(context)
    }

    override fun handlePlaySong() {
        view.displayLocalSong()
        view.handleButton()
    }

}