package com.dauma.grokimkartu.repositories.conversations.entities

import android.graphics.Bitmap
import com.dauma.grokimkartu.repositories.players.PlayersErrors

class MessageUserIcon(private val loader: (onComplete: (Bitmap?, PlayersErrors?) -> Unit) -> Unit) {
    var icon: Bitmap? = null
        private set
    var status: MessageUserIconStatus = MessageUserIconStatus.NEED_TO_DOWNLOAD
        private set

    fun loadIfNeeded(onComplete: (Bitmap?, PlayersErrors?) -> Unit) {
        if (status == MessageUserIconStatus.NEED_TO_DOWNLOAD) {
            status = MessageUserIconStatus.DOWNLOAD_IN_PROGRESS
            this.loader() { photo, e ->
                icon = photo
                if (photo != null) {
                    status = MessageUserIconStatus.DOWNLOADED_ICON_SET
                } else {
                    status = MessageUserIconStatus.DOWNLOADED_ICON_NOT_SET
                }
                onComplete(photo, e)
            }
        } else {
            onComplete(icon, null)
        }
    }
}