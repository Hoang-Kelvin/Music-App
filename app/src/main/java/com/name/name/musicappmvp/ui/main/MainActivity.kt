package com.name.name.musicappmvp.ui.main

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.text.TextUtils
import android.view.View
import android.widget.SeekBar
import android.widget.SeekBar.*
import com.name.name.musicappmvp.R
import com.name.name.musicappmvp.data.model.LocalSong
import com.name.name.musicappmvp.data.repository.SongRepository
import com.name.name.musicappmvp.service.PlaySongService
import com.name.name.musicappmvp.ui.adapter.LocalMusicAdapter
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity(), View.OnClickListener, LocalMusicInterface.View {
    private var localSongs = mutableListOf<LocalSong>()
    private var isBind = false
    private var localMusicPresenter: LocalMusicPresenter? = null
    private var playSongService: PlaySongService? = null

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as PlaySongService.SongBinder
            playSongService = binder.getService()
            playSongService?.let { it.bindList(localSongs) }
            isBind = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isBind = false
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        checkPermission()
        localMusicPresenter = LocalMusicPresenter(this, SongRepository())
        displayLocalSong()
    }

    override fun onStart() {
        super.onStart()
        Intent(this, PlaySongService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.buttonPlay -> {
                if (playSongService?.isPlaying == false) {
                    if (playSongService?.isPause == true) playSongService?.start()
                    playSongService?.playing = true
                    buttonPlay.setImageResource(R.drawable.ic_pause)
                } else {
                    playSongService?.pause()
                    playSongService?.playing = false
                    buttonPlay.setImageResource(R.drawable.ic_play)
                }
            }
            R.id.buttonNext -> {
                PlaySongService.position++
                if (PlaySongService.position == localSongs.size) PlaySongService.position = 0
                resetPlayingState()
            }
            R.id.buttonPrev -> {
                PlaySongService.position--
                if (PlaySongService.position < 0) PlaySongService.position = localSongs.size - 1
                resetPlayingState()
            }
        }
    }

    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ((checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
                return
            }
        }
    }

    override fun displayLocalSong() {
        localSongs = localMusicPresenter?.getLocalSong(this) as MutableList<LocalSong>
        localSongs.sortWith(compareBy({ it.title }, { it.title }))
        val adapter = LocalMusicAdapter(::showInformationSong)
        adapter.sendList(localSongs)
        recyclerSongs.adapter = adapter
        handleButton()
    }

    override fun handleButton() {
        buttonPlay.setOnClickListener(this)
        buttonNext.setOnClickListener(this)
        buttonPrev.setOnClickListener(this)
        songDuration.setOnSeekBarChangeListener(object : OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                playSongService?.rewind(progress)
                duration.text = playSongService?.timeTracking()
                if (playSongService?.isNext == true){
                    textNameSong.text = getString(R.string.text_playing).plus(" " +localSongs[PlaySongService.position].title)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(connection)
        isBind = false
    }

    private fun resetPlayingState() {
        songDuration.progress = 0
        playSongService?.playSong()
        songDuration.max = localSongs[PlaySongService.position].duration
        textNameSong.text = getString(R.string.text_playing).plus(" " +localSongs[PlaySongService.position].title)
    }

    private fun showInformationSong(){
        playSongService?.playSong()
        playSongService?.playing = true
        buttonPlay.setImageResource(R.drawable.ic_pause)
        textNameSong.text = getString(R.string.text_playing).plus(" " +localSongs[PlaySongService.position].title)
        trackingDurationBar()
    }

    private fun trackingDurationBar() {
        thread(start = true, name = "playingThread") {
            Timer().scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    songDuration.progress = playSongService?.currentPosition ?: 0
                    duration.text = playSongService?.timeTracking()
                    songDuration.max = localSongs[PlaySongService.position].duration
                }
            }, 0, 1000)
        }
    }
}