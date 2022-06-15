package com.dauma.grokimkartu.general.utils.sharedstorage

import android.content.Context

interface SharedStorageUtils {
    fun save(context: Context, key: String, value: String)
    fun getEntry(context: Context, key: String) : String?
    fun remove(context: Context, key: String)
}