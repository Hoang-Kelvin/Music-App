package com.name.name.musicappmvp.service

import android.app.Service
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.os.PowerManager
import android.provider.MediaStore
import android.util.Log
import android.widget.MediaController
import com.name.name.musicappmvp.data.model.Song
import com.name.name.musicappmvp.ultis.ChannelEntity
import com.name.name.musicappmvp.ultis.DataManager
import com.name.name.musicappmvp.ultis.DataManager.position
import java.lang.Exception

class PlaySongService : Service(), MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener,
    MediaController.MediaPlayerControl {
    private val binder = SongBinder()
    private var notificationChannel: NotificationChannel? = null

    private var player = DataManager.player
    private var listSong = mutableListOf<Song>()
    var isPause = false
    var playing = false
    var isNext = false

    val bindList = fun(list: MutableList<Song>) { listSong = list }

    inner class SongBinder : Binder() {
        fun getService(): PlaySongService = this@PlaySongService
    }

    override fun onCreate() {
        super.onCreate()
        if (notificationChannel == null) {
            initMusicPlayer()
            notificationChannel = NotificationChannel()
        }
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onUnbind(intent: Intent?): Boolean {
        player.stop()
        player.release()
        return false
    }

    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        mp?.reset()
        return false
    }

    override fun onCompletion(mp: MediaPlayer?) {
        position++
        if (position == listSong.size) position = 0
        playSong()
        isNext = true
    }

    override fun start() {
        isPause = false
        player.start()
    }

    override fun pause() {
        isPause = true
        player.pause()
    }

    override fun getDuration(): Int = listSong[position].duration

    override fun getCurrentPosition(): Int = player.currentPosition

    override fun seekTo(pos: Int) = player.seekTo(pos)

    override fun isPlaying(): Boolean = playing

    override fun getBufferPercentage(): Int = 0

    override fun canPause(): Boolean = true

    override fun canSeekBackward(): Boolean = true

    override fun canSeekForward(): Boolean = true

    override fun getAudioSessionId(): Int = player.audioSessionId

    override fun onDestroy() {
        super.onDestroy()
        stopForeground(true)
        player.release()
    }

    private fun initMusicPlayer() {
        player.setWakeMode(applicationContext, PowerManager.PARTIAL_WAKE_LOCK)
        player.setAudioStreamType(AudioManager.STREAM_MUSIC)
        player.setOnCompletionListener(this)
        player.setOnErrorListener(this)
        player.isLooping = true
    }

    private fun startNotification() {
        notificationChannel?.songNotification(this, listSong[position].title)
        startForeground(ChannelEntity.NOTIFY_ID, NotificationChannel.notification1)
    }

    fun playSong() {
        player.reset()
        val currentSong = listSong[position].id.toLong()
        val uri =
            ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, currentSong)
        try {
            player.setDataSource(applicationContext, uri)
        } catch (e: Exception) {
            Log.e("MUSIC SERVICE", "Error setting data source", e)
        }
        player.setOnPreparedListener { mp -> mp.start() }
        player.prepare()
        startNotification()
        playing = true
    }

    fun timeTracking(): String {
        val second = if ((currentPosition / 1000) % 60 < 10)
            "0" + (currentPosition / 1000) % 60 else (currentPosition / 1000) % 60
        val minute = (currentPosition / 60000) % 60
        return "$minute:$second"
    }

    fun rewind(progress: Int) {
        if (progress > currentPosition + 1500 || progress < currentPosition - 1500) seekTo(progress)
    }

    companion object {
        fun getIntent(context: Context): Intent = Intent(context, PlaySongService::class.java)
    }
}
