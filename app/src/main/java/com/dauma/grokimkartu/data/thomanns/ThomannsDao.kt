package com.dauma.grokimkartu.data.thomanns

import com.dauma.grokimkartu.data.thomanns.entities.ThomannDao

interface ThomannsDao {
    fun createThomann(thomann: ThomannDao, onComplete: (Boolean, Exception?) -> Unit)
    fun updateThomann(thomann: ThomannDao, onComplete: (Boolean, Exception?) -> Unit)
    fun deleteThomann(thomannId: String, onComplete: (Boolean, Exception?) -> Unit)
    fun getThomanns(onComplete: (Boolean, List<ThomannDao>?, Exception?) -> Unit)
    fun getThomann(id: String, onComplete: (ThomannDao?, Exception?) -> Unit)
}