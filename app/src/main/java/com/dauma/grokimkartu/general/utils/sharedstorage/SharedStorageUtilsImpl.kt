package com.dauma.grokimkartu.general.utils.sharedstorage

import android.content.Context

class SharedStorageUtilsImpl(
    private val context: Context
) : SharedStorageUtils {
    private val sharedPreferences = context.getSharedPreferences(ID, Context.MODE_PRIVATE)

    companion object {
        private const val ID = "GK_SHARED_STORAGE"
    }

    override fun save(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    override fun getEntry(key: String): String? {
        return sharedPreferences.getString(key, null)
    }

    override fun remove(key: String) {
        sharedPreferences.edit().remove(key).apply()
    }
}