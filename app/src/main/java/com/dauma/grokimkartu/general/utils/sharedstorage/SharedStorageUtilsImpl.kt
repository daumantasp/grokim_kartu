package com.dauma.grokimkartu.general.utils.sharedstorage

import android.content.Context
import android.content.SharedPreferences

class SharedStorageUtilsImpl : SharedStorageUtils {
    companion object {
        private const val ID = "GK_SHARED_STORAGE"
    }

    override fun save(context: Context, key: String, value: String) {
        getSharedPreferences(context).edit().putString(key, value).apply()
    }

    override fun getEntry(context: Context, key: String): String? {
        return getSharedPreferences(context).getString(key, null)
    }

    override fun remove(context: Context, key: String) {
        getSharedPreferences(context).edit().remove(key).apply()
    }

    private fun getSharedPreferences(context: Context) : SharedPreferences {
        return context.getSharedPreferences(ID, Context.MODE_PRIVATE)
    }
}