package com.zelgius.throwingthings

import android.content.Context
import android.media.RingtoneManager
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import com.zelgius.throwingthings.viewModel.Media
import com.zelgius.throwingthings.viewModel.toMimeType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class MediaRepository(private val context: Context) {
    suspend fun getMediaList() =
        withContext(Dispatchers.Default) {
            val list = mutableListOf<Media>()
            val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            val projection = arrayOf(
                MediaStore.Audio.AudioColumns._ID,
                MediaStore.Audio.AudioColumns.TITLE,
                MediaStore.Audio.AudioColumns.MIME_TYPE
            )

            val c = context.contentResolver.query(
                uri,
                projection,
                "${MediaStore.Audio.AudioColumns.MIME_TYPE}  = '${"mp3".toMimeType()}'",
                null,
                null
            )

            if (c != null) {
                while (c.moveToNext()) {
                    val audioModel = Media(
                        id = c.getLong(0),
                        title = c.getString(1)
                    )
                    list += audioModel
                    Log.d(this::class.simpleName, c.getString(2))

                }
                c.close()
            }

            list
        }

    suspend fun getCurrentNotification(): String? =
        withContext(Dispatchers.Default) {
            val defaultRingtoneUri = RingtoneManager.getActualDefaultRingtoneUri(
                context,
                RingtoneManager.TYPE_NOTIFICATION
            )
            RingtoneManager.getRingtone(context, defaultRingtoneUri)
                .getTitle(context)
        }

    suspend fun setCurrentNotification(uri: Uri) =
        withContext(Dispatchers.Default) {
            RingtoneManager.setActualDefaultRingtoneUri(
                context,
                RingtoneManager.TYPE_NOTIFICATION,
                uri
            )
        }
}