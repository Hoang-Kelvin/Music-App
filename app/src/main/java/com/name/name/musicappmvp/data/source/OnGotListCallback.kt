package com.name.name.musicappmvp.data.source

import com.name.name.musicappmvp.data.model.Song
import java.lang.Exception

interface OnGotListCallback {
    fun onSuccess(list: List<Song>)
    fun onFailure(exception: Exception)
}
