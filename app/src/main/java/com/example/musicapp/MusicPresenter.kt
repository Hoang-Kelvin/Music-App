package com.example.musicapp

import android.widget.MediaController

class MusicPresenter(var mView: MusicInterface.View,var mService: MusicService,var mListSong: ArrayList<Song>) :
    MusicInterface.Presenter, MediaController.MediaPlayerControl {
    var playing = false
    var isPause = false

    override fun startNewSong() {
        mView.displaySong()
    }

    fun timeTracking(): String {
        val second = if ((currentPosition / 1000) % 60 < 10)
            "0" + (currentPosition / 1000) % 60 else (currentPosition / 1000) % 60
        val minute = (currentPosition / 60000) % 60
        return "$minute:$second"
    }

    override fun handleSong(progress: Int) {
        if (progress > currentPosition + 1500 || progress < currentPosition - 1500) seekTo(progress)
    }

    override fun start() {
        isPause = false
        mService.mPlayer.start()
    }

    override fun pause() {
        isPause = true
        mService.mPlayer.pause()
    }

    override fun getDuration(): Int {
        return mListSong[mService.mPosition].duration
    }

    override fun getCurrentPosition(): Int {
        return mService.mPlayer.currentPosition
    }

    override fun seekTo(pos: Int) {
        mService.mPlayer.seekTo(pos)
    }

    override fun isPlaying(): Boolean {
        return playing
    }

    override fun getBufferPercentage(): Int {
        return 0
    }

    override fun canPause(): Boolean {
        return true
    }

    override fun canSeekBackward(): Boolean {
        return true
    }

    override fun canSeekForward(): Boolean {
        return true
    }

    override fun getAudioSessionId(): Int {
        return mService.mPlayer.audioSessionId
    }
}
