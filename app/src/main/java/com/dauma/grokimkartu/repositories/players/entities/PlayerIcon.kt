package com.dauma.grokimkartu.repositories.players.entities

import android.graphics.Bitmap
import com.dauma.grokimkartu.repositories.players.PlayersErrors

class PlayerIcon(private val loader: (onComplete: (Bitmap?, PlayersErrors?) -> Unit) -> Unit) {
    var icon: Bitmap? = null
        private set
    var status: PlayerIconStatus = PlayerIconStatus.NEED_TO_DOWNLOAD
        private set

    fun loadIfNeeded(onComplete: (Bitmap?, PlayersErrors?) -> Unit) {
        if (status == PlayerIconStatus.NEED_TO_DOWNLOAD) {
            status = PlayerIconStatus.DOWNLOAD_IN_PROGRESS
            this.loader() { photo, e ->
                icon = photo
                if (photo != null) {
                    status = PlayerIconStatus.DOWNLOADED_ICON_SET
                } else {
                    status = PlayerIconStatus.DOWNLOADED_ICON_NOT_SET
                }
                onComplete(photo, e)
            }
        } else {
            onComplete(icon, null)
        }
    }
}