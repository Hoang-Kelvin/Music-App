package com.name.name.musicappmvp.data.repository

import android.os.AsyncTask
import com.name.name.musicappmvp.data.model.Song
import com.name.name.musicappmvp.data.source.OnGotListCallback
import java.lang.Exception

class TakeSongFromLocal(
    private val callback: OnGotListCallback,
    private val getSong: () -> List<Song>
) : AsyncTask<Unit, Unit, List<Song>>()
{
    override fun doInBackground(vararg params: Unit?): List<Song> {
        var list = listOf<Song>()
        try {
           list = getSong()
        }catch (e: Exception){
            callback.onFailure(e)
        }
        return list
    }

    override fun onPostExecute(result: List<Song>?) {
        super.onPostExecute(result)
        result?.let { callback.onSuccess(it) }
    }
}
