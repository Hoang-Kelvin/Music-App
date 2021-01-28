package com.name.name.musicappmvp.ui.main

import com.name.name.musicappmvp.data.model.Song
import com.name.name.musicappmvp.data.repository.LocalSongRepository
import com.name.name.musicappmvp.data.source.OnGotListCallback
import java.lang.Exception

class MainLocalMusicPresenter(
    private val view: MainLocalMusicContract.View,
    private val localSongRepository: LocalSongRepository
) :
    MainLocalMusicContract.Presenter {
    override fun getAllLocalSong() {
        localSongRepository.getLocalSongs(object : OnGotListCallback {
            override fun onSuccess(list: List<Song>) {
                view.displayLocalSong(list)
            }

            override fun onFailure(exception: Exception) {
                //showException()
            }
        })
    }

    override fun playChosenSong() {
        view.setPauseButton()
    }

    override fun stopSongPlaying() {
        view.setPlayButton()
    }
}
