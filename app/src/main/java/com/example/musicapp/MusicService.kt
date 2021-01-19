package com.example.musicapp

import android.app.Service
import android.content.ContentUris
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.os.PowerManager
import android.provider.MediaStore
import android.util.Log

class MusicService() : Service(), MediaPlayer.OnErrorListener,
    MediaPlayer.OnCompletionListener {

    private val mBinder = SongBinder()
    var songs: List<Song> = ArrayList()
    lateinit var player: MediaPlayer

    var mPosition: Int = 0
    var isNext = false

    val bindList = fun(list: List<Song>) { songs = list }

    override fun onBind(intent: Intent?): IBinder = mBinder

    override fun onUnbind(intent: Intent?): Boolean {
        player.stop()
        player.release()
        return false
    }

    override fun onCreate() {
        super.onCreate()
        player = MediaPlayer()
        initMusicPlayer()
    }

    private fun initMusicPlayer() {
        player.setWakeMode(applicationContext, PowerManager.PARTIAL_WAKE_LOCK)
        player.setAudioStreamType(AudioManager.STREAM_MUSIC)
        player.setOnCompletionListener(this)
        player.setOnErrorListener(this)
        player.isLooping = true
    }

    inner class SongBinder : Binder() {
        fun getService(): MusicService = this@MusicService
    }

    fun playSong() {
        player.reset()
        val currentSong = songs[mPosition].id.toLong()
        val uri =
            ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, currentSong)
        try {
            player.setDataSource(applicationContext, uri)
        } catch (e: Exception) {
            Log.e("MUSIC SERVICE", "Error setting data source", e)
        }
        player.setOnPreparedListener { mp -> mp?.start() }
        player.prepare()
    }

    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        mp?.reset()
        return false
    }

    override fun onCompletion(mp: MediaPlayer?) {
        mPosition++
        if (mPosition == songs.size) mPosition = 0
        playSong()
        isNext = true
    }
}
