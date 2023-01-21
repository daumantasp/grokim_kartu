package com.dauma.grokimkartu.general.utils.app

import android.content.Context
import android.provider.Settings

class AppUtilsImpl(
    private val context: Context
): AppUtils {
    override fun deviceId(): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }
}