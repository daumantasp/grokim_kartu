package com.dauma.grokimkartu.ui.main.adapters

import android.graphics.Bitmap
import com.dauma.grokimkartu.repositories.thomanns.entities.ThomannUser

open class ThomannDetailsListData
class ThomannDetailsListPhotoData(val name: String, val photo: Bitmap?, val isLocked: Boolean) : ThomannDetailsListData()
class ThomannDetailsListRowData(val title: String, val value: String) : ThomannDetailsListData()
class ThomannDetailsListUserData(val user: ThomannUser) : ThomannDetailsListData()
class ThomannDetailsListButtonData(val title: String, val onClick: () -> Unit) : ThomannDetailsListData()