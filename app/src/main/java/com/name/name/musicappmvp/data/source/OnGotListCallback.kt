package com.name.name.musicappmvp.data.source

import com.name.name.musicappmvp.data.model.LocalSong
import java.lang.Exception

interface OnGotListCallback {
    fun onSuccess(list: List<LocalSong>)
    fun onFailure(exception: Exception)
}
