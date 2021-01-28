package com.name.name.musicappmvp.data.source.local

import android.content.Context
import android.os.Build
import android.provider.MediaStore
import com.name.name.musicappmvp.data.model.Song
import com.name.name.musicappmvp.data.repository.TakeSongFromLocal
import com.name.name.musicappmvp.data.source.LocalSongDataSource
import com.name.name.musicappmvp.data.source.OnGotListCallback

class LocalSource(private val context: Context) : LocalSongDataSource.Local {
    override fun getLocalSong(callback: OnGotListCallback) {
        TakeSongFromLocal(callback, ::getSong).execute()
    }

    private fun getSong(): List<Song> {
        val listSong = mutableListOf<Song>()
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
        val cursor = context.contentResolver.query(uri, projection, selection, null, null)
        cursor?.use {
            val id = cursor.getColumnIndex(MediaStore.Audio.Media._ID)
            val title = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val duration = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)
            while (cursor.moveToNext()) {
                listSong.add(
                    Song(
                        cursor.getInt(id),
                        cursor.getString(title),
                        cursor.getInt(duration)
                    )
                )
            }
        }
        return listSong
    }

    companion object {
        private var instance: LocalSource? = null

        fun getInstanceLocalSource(context: Context) =
            instance ?: LocalSource(context).also { instance = it }
    }
}
