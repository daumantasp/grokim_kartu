package com.dauma.grokimkartu.repositories.thomanns.entities

import android.graphics.Bitmap
import com.dauma.grokimkartu.repositories.thomanns.ThomannsErrors

// TODO: refactor, duplicates player icon
class ThomannPlayerIcon(private val loader: (onComplete: (Bitmap?, ThomannsErrors?) -> Unit) -> Unit) {
    var icon: Bitmap? = null
        private set
    var status: ThomannPlayerIconStatus = ThomannPlayerIconStatus.NEED_TO_DOWNLOAD
        private set

    fun loadIfNeeded(onComplete: (Bitmap?, ThomannsErrors?) -> Unit) {
        if (status == ThomannPlayerIconStatus.NEED_TO_DOWNLOAD) {
            status = ThomannPlayerIconStatus.DOWNLOAD_IN_PROGRESS
            this.loader() { photo, e ->
                icon = photo
                if (photo != null) {
                    status = ThomannPlayerIconStatus.DOWNLOADED_ICON_SET
                } else {
                    status = ThomannPlayerIconStatus.DOWNLOADED_ICON_NOT_SET
                }
                onComplete(photo, e)
            }
        } else {
            onComplete(icon, null)
        }
    }
}