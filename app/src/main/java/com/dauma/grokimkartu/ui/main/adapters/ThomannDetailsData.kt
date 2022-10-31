package com.dauma.grokimkartu.ui.main.adapters

import android.graphics.Bitmap
import com.dauma.grokimkartu.repositories.thomanns.entities.ThomannUser

data class ThomannDetailsPhotoData(
    val name: String?,
    val photo: Bitmap?,
    val isLocked: Boolean?,
    val onClick: () -> Unit
)

data class ThomannDetailsRowData(
    val title: String,
    val value: String?
)

data class ThomannDetailsStatusData(
    val title: String,
    val isLocked: Boolean?,
    val onClick: () -> Unit
)

data class ThomannDetailsUserData(val user: ThomannUser)

data class ThomannDetailsButtonData(
    val title: String,
    val isCancelAction: Boolean,
    val onClick: () -> Unit
)