package com.example.musicapp

import android.widget.MediaController

class MusicPresenter(
    var view: MusicInterface.View,
    var mService: MusicService,
    var listSong: List<Song>
) : MusicInterface.Presenter, MediaController.MediaPlayerControl {
    var playing = false
    var isPause = false

    override fun startNewSong() {
        view.displaySong()
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
        mService.player?.start()
    }

    override fun pause() {
        isPause = true
        mService.player?.pause()
    }

    override fun getDuration(): Int {
        return listSong[mService.mPosition].duration
    }

    override fun getCurrentPosition(): Int {
        val player = mService.player
        return player?.currentPosition ?: 0
    }

    override fun seekTo(pos: Int) {
        mService.player?.seekTo(pos)
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
        val player = mService.player
        return player?.audioSessionId ?: 0
    }
}
