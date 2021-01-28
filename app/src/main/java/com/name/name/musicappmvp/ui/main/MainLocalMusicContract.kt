package com.name.name.musicappmvp.ui.main

import com.name.name.musicappmvp.data.model.Song

class MainLocalMusicContract {
    interface View {
        fun setPlayButton()
        fun setPauseButton()
        fun displayLocalSong(list: List<Song>)
    }

    interface Presenter {
        fun getAllLocalSong()
        fun playChosenSong()
        fun stopSongPlaying()
    }
}
