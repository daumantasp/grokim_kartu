package com.dauma.grokimkartu.general

import android.graphics.Bitmap

class IconLoader(private val download: suspend ((Bitmap?) -> Unit) -> Unit) {
    var icon: Bitmap? = null
        private set
    var status: IconStatus = IconStatus.NEED_TO_DOWNLOAD
        private set

    suspend fun loadIcon(onComplete: (Bitmap?) -> Unit) {
        if (status == IconStatus.NEED_TO_DOWNLOAD) {
            this.download() { icon ->
                this.icon = icon
                if (icon != null) {
                    status = IconStatus.ICON_DOWNLOADED
                } else {
                    status = IconStatus.ICON_NOT_SET
                }
                onComplete(icon)
            }
        } else {
            onComplete(icon)
        }
    }
}

