package com.zelgius.throwingthings.entity

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.annotation.RawRes


abstract class AbstractSound (
    @DrawableRes val drawable: Int,
    @DrawableRes val drawableAmbient: Int,
    @RawRes val sound: Int
){


    fun play(context: Context, mediaPlayer: MediaPlayer?): MediaPlayer =
        if(mediaPlayer == null) {
            MediaPlayer.create(context, sound).apply {
                start()
            }
        } else {
            val resourceUri = Uri.parse("android.resource://${context.packageName}/$sound")

            mediaPlayer.apply {
                reset()
                context.resources.openRawResourceFd(sound)?.let {
                    setDataSource(
                        it.fileDescriptor,
                        it.startOffset,
                        it.length
                    )
                    it.close()
                }
                prepareAsync()
            }
        }
}