package com.zelgius.throwingthings.viewModel

import android.app.Application
import android.content.ContentUris
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.liveData
import com.zelgius.throwingthings.MediaRepository
import java.util.*


class NotificationViewModel(val app: Application) : AndroidViewModel(app) {

    private val repository = MediaRepository(app)

    fun getMediaFiles() =
        liveData {
            emit(repository.getMediaList())
        }

    fun getCurrentNotificationMedia() =
        liveData {
            emit(repository.getCurrentNotification())
        }

    fun setCurrentNotification(uri: Uri) =
        liveData {
            emit(repository.setCurrentNotification(uri))
        }
}

data class Media(val id: Long, val title: String) {
    val uri: Uri
        get() = ContentUris.withAppendedId(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id
        )
}

fun String.toMimeType() =
    MimeTypeMap.getSingleton().getMimeTypeFromExtension(toLowerCase(Locale.ROOT))
