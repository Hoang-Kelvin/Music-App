package com.name.name.musicappmvp.ui.main

import com.name.name.musicappmvp.data.model.LocalSong

class MainLocalMusicContract {
    interface View {
        fun setPlayButton()
        fun setPauseButton()
    }

    interface Presenter {
        fun getAllLocalSong(): List<LocalSong>
        fun playChosenSong()
        fun stopSongPlaying()
    }
}
