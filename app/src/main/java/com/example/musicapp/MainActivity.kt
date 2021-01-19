package com.example.musicapp

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
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import java.util.Timer
import java.util.TimerTask
import kotlin.collections.ArrayList
import kotlin.concurrent.thread

data class Song(val id: Int, val title: String, val duration: Int)

class MainActivity : AppCompatActivity(), MusicInterface.View, MusicInterface,
    View.OnClickListener {
    private val listSong: MutableList<Song> = ArrayList()

    private lateinit var adapter: Adapter
    private lateinit var mService: MusicService
    private lateinit var musicPresenter: MusicPresenter

    private var isBind = false

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MusicService.SongBinder
            mService = binder.getService()
            mService.bindList(listSong)
            isBind = true
            musicPresenter = MusicPresenter(this@MainActivity, mService, listSong)
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
        getSong()

        //get all View at the same time as app starting
        songInfo.isSelected = true

        //Sort music list follow alphabet rule
        listSong.sortedWith(compareBy({ it.title }, { it.title }))
        
        //Add song's list to RecycleView and show it on screen
        adapter = Adapter(listSong, this)
        listItem.adapter = adapter
        listItem.layoutManager = LinearLayoutManager(this)
        //Now call the control behavior method
        songController()
    }

    override fun onStart() {
        super.onStart()
        Intent(this, MusicService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }

    }

    //Return a list of song when open an application, so
    //this method should call only once, call it when refresh song's list otherwise
    private fun getSong() {
        //check and get uri depend on android version of device
        val uri =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Audio.Media.getContentUri(
                    MediaStore.VOLUME_EXTERNAL
                )
            } else {
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            }
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.SIZE
        )
        val selection = MediaStore.Audio.Media.IS_MUSIC + " != 0"
        val musicCursor = contentResolver.query(uri, projection, selection, null, null)
        musicCursor?.use {
            val id = musicCursor.getColumnIndex(MediaStore.Audio.Media._ID)
            val title = musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val duration = musicCursor.getColumnIndex(MediaStore.Audio.Media.DURATION)
            while (musicCursor.moveToNext()) {
                listSong.add(
                    Song(
                        musicCursor.getInt(id),
                        musicCursor.getString(title),
                        musicCursor.getInt(duration)
                    )
                )
            }
        }
    }

    //If song item was clicked, it will be played immediately
    //and set its duration to seek bar, the seek bar will auto-change
    //every 1 second to follow the song
    override fun displaySong() {
        mService.mPosition = adapter.currentPosition
        mService.playSong()
        musicPresenter.playing = true
        buttonPlay.setImageResource(R.drawable.ic_pause)
        //Using other thread to show the change of SeekBar and current duration
        thread(start = true, name = "playingThread") {
            Timer().scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    songDuration.progress = musicPresenter.currentPosition
                    duration.text = musicPresenter.timeTracking()
                    songDuration.max = musicPresenter.duration
                }
            }, 0, 1000)
        }
        songInfo.text = getString(R.string.playing).plus(listSong[mService.mPosition].title)
    }

    private fun songController() {

        //button.setOnClickListener { this } not working
        buttonPlay.setOnClickListener { onClick(it) }
        buttonNext.setOnClickListener { onClick(it) }
        buttonPrev.setOnClickListener { onClick(it) }
        songDuration.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                //Catch the SeekBar changed to rewind, fast forward or auto play next song
                musicPresenter.handleSong(progress)
                duration.text = musicPresenter.timeTracking()
                if (mService.isNext) {
                    songInfo.text =
                        getString(R.string.playing).plus(" " + listSong[mService.mPosition].title)
                    mService.isNext = false
                }
            }

            //Using those method later on
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    //This application need some permission to read data, a dialog will be trigger
    //read external storage permit if needed
    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ((checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
                return
            }
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.buttonPlay -> {
                if (!musicPresenter.playing) {
                    if (musicPresenter.isPause) musicPresenter.start()
                    musicPresenter.playing = true
                    buttonPlay.setImageResource(R.drawable.ic_pause)
                } else {
                    musicPresenter.pause()
                    musicPresenter.playing = false
                    buttonPlay.setImageResource(R.drawable.ic_play)
                }
            }
            R.id.buttonNext -> {
                mService.mPosition++
                if (mService.mPosition == mService.songs.size) mService.mPosition = 0
                resetPlayingState()
            }
            R.id.buttonPrev -> {
                mService.mPosition--
                if (mService.mPosition < 0) mService.mPosition = mService.songs.size - 1
                resetPlayingState()
            }
        }
    }

    private fun resetPlayingState() {
        songDuration.progress = 0
        mService.playSong()
        songDuration.max = musicPresenter.duration
        songInfo.text = getString(R.string.playing).plus(" " + listSong[mService.mPosition].title)
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(connection)
        isBind = false
    }
}
