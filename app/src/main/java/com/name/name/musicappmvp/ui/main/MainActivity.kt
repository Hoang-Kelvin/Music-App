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
import android.view.View
import android.widget.SeekBar
import android.widget.SeekBar.*
import com.name.name.musicappmvp.R
import com.name.name.musicappmvp.data.model.LocalSong
import com.name.name.musicappmvp.data.repository.LocalSongRepository
import com.name.name.musicappmvp.data.source.local.LocalSource
import com.name.name.musicappmvp.service.PlaySongService
import com.name.name.musicappmvp.ui.adapter.LocalMusicAdapter
import com.name.name.musicappmvp.ultis.DataManager.position
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity(), View.OnClickListener, MainLocalMusicContract.View {
    private var localSongs = mutableListOf<LocalSong>()
    private var isBind = false
    private var mainLocalMusicPresenter: MainLocalMusicContract.Presenter? = null
    private var playSongService: PlaySongService? = null
    private var serviceIntent: Intent? = null

    private var localMusicAdapter = LocalMusicAdapter(::showInformationSong)
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
        textNameSong.isSelected = true
        mainLocalMusicPresenter =
            MainLocalMusicPresenter(
                this,
                LocalSongRepository.getInstanceRepository(LocalSource.getInstanceLocalSource(this))
            )
        handleButton()
        displayLocalSong()
    }

    override fun onStart() {
        super.onStart()
        serviceIntent = PlaySongService.getIntent(context = this)
        bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.buttonPlay -> playSongService?.isPlaying?.let { setPlayingState(it) }
            R.id.buttonNext -> {
                position++
                if (position == localSongs.size) position = 0
                resetPlayingState()
            }
            R.id.buttonPrev -> {
                position--
                if (position < 0) position = localSongs.size - 1
                resetPlayingState()
            }
        }
    }

    override fun setPlayButton() {
        buttonPlay.setImageResource(R.drawable.ic_play)
    }

    override fun setPauseButton() {
        buttonPlay.setImageResource(R.drawable.ic_pause)
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(connection)
        isBind = false
    }

    private fun resetPlayingState() {
        songDuration.progress = 0
        playSongService?.playSong()
        songDuration.max = localSongs[position].duration
        val nameSong = localSongs[position].title
        textNameSong.text = getString(R.string.text_playing_state, nameSong)
    }

    private fun showInformationSong() {
        playSongService?.playSong()
        trackingDurationBar()
        mainLocalMusicPresenter?.playChosenSong()
        val nameSong = localSongs[position].title
        textNameSong.text = getString(R.string.text_playing_state, nameSong)
    }

    private fun trackingDurationBar() {
        thread(start = true, name = "playingThread") {
            Timer().scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    songDuration.progress = playSongService?.currentPosition ?: 0
                    duration.text = playSongService?.timeTracking()
                    songDuration.max = localSongs[position].duration
                }
            }, 0, 1000)
        }
    }

    private fun handleButton() {
        buttonPlay.setOnClickListener(this)
        buttonNext.setOnClickListener(this)
        buttonPrev.setOnClickListener(this)
        songDuration.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                playSongService?.rewind(progress)
                duration.text = playSongService?.timeTracking()
                if (playSongService?.isNext == true) {
                    val nameSong = localSongs[position].title
                    textNameSong.text = getString(R.string.text_playing_state, nameSong)
                    playSongService?.isNext = false
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })
    }

    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ((checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
            }
        }
    }

    private fun displayLocalSong() {
        localSongs = mainLocalMusicPresenter?.getAllLocalSong()?.toMutableList()!!
        localSongs.sortWith(compareBy({ it.title }, { it.title }))
        localMusicAdapter.sendList(localSongs)
        recyclerSongs.adapter = localMusicAdapter
    }

    private fun setPlayingState(boolean: Boolean) {
        if (!boolean) {
            if (playSongService?.isPause == true) playSongService?.start()
            playSongService?.playing = true
            mainLocalMusicPresenter?.playChosenSong()
        } else {
            playSongService?.pause()
            playSongService?.playing = false
            mainLocalMusicPresenter?.stopSongPlaying()
        }
    }
}
