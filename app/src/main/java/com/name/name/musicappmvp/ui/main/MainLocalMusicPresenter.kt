package com.name.name.musicappmvp.ui.main

import com.name.name.musicappmvp.data.model.LocalSong
import com.name.name.musicappmvp.data.repository.LocalSongRepository
import com.name.name.musicappmvp.data.source.OnGotListCallback
import java.lang.Exception

class MainLocalMusicPresenter(
    private val view: MainLocalMusicContract.View,
    private val localSongRepository: LocalSongRepository
) :
    MainLocalMusicContract.Presenter {
    private var listSongs = listOf<LocalSong>()

    override fun getAllLocalSong(): List<LocalSong> {
        listSongs = localSongRepository.getLocalSong(object : OnGotListCallback {
            override fun onSuccess(list: List<LocalSong>) {
                listSongs = list
            }

            override fun onFailure(exception: Exception) {
                println(exception.toString())
            }
        })
        return listSongs
    }

    override fun playChosenSong() {
        view.setPauseButton()
    }

    override fun stopSongPlaying() {
        view.setPlayButton()
    }
}
