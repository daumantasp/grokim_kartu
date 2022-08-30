package com.dauma.grokimkartu.general.utils.sharedstorage

interface SharedStorageUtils {
    fun save(key: String, value: String)
    fun getEntry(key: String) : String?
    fun remove(key: String)
}