package com.example.musicapp

interface MusicInterface {
    interface View {
        fun displaySong()
    }
    interface Presenter {
        fun startNewSong()
        fun handleSong(progress: Int)
    }
}