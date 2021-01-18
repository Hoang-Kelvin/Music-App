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

class MusicService : Service(), MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {
    var mSongs: ArrayList<Song> = ArrayList()
    private val mBinder = SongBinder()

    var mPosition: Int = 0
    var mPlayer: MediaPlayer = MediaPlayer()
    var isNext = false

    override fun onBind(intent: Intent?): IBinder {
        return mBinder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        mPlayer.stop()
        mPlayer.release()
        return false
    }

    override fun onCreate() {
        super.onCreate()
        initMusicPlayer()
    }

    private fun initMusicPlayer() {
        mPlayer.setWakeMode(applicationContext, PowerManager.PARTIAL_WAKE_LOCK)
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
        mPlayer.setOnCompletionListener(this)
        mPlayer.setOnErrorListener(this)
        mPlayer.isLooping = true
    }

    inner class SongBinder : Binder() {
        fun getService(): MusicService = this@MusicService
    }

    fun bindList(list: ArrayList<Song>) {
        mSongs = list
    }

    fun playSong() {
        mPlayer.reset()
        val currentSong = mSongs[mPosition].id.toLong()
        val uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, currentSong)
        try {
            mPlayer.setDataSource(applicationContext, uri)
        } catch (e: Exception) {
            Log.e("MUSIC SERVICE", "Error setting data source", e)
        }
        mPlayer.setOnPreparedListener { mp -> mp?.start() }
        mPlayer.prepare()
    }

    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        mp?.reset()
        return false
    }

    override fun onCompletion(mp: MediaPlayer?) {
        mPosition++
        if (mPosition == mSongs.size) mPosition = 0
        playSong()
        isNext = true
    }
}