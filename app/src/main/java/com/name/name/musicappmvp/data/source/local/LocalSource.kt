package com.name.name.musicappmvp.data.source.local

import android.content.Context
import android.os.Build
import android.provider.MediaStore
import com.name.name.musicappmvp.data.model.LocalSong
import com.name.name.musicappmvp.data.source.SongDataSource

class LocalSource : SongDataSource.Local {
    override fun getLocalSong(context:Context): MutableList<LocalSong> {
        val listSong = mutableListOf<LocalSong>()
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
                    LocalSong(
                        cursor.getInt(id),
                        cursor.getString(title),
                        cursor.getInt(duration)
                    )
                )
            }
        }
        return listSong
    }

}