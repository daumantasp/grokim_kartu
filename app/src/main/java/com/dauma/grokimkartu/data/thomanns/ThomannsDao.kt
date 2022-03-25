package com.dauma.grokimkartu.data.thomanns

import com.dauma.grokimkartu.data.thomanns.entities.ThomannDao
import com.dauma.grokimkartu.data.thomanns.entities.ThomannUserDao

interface ThomannsDao {
    fun createThomann(thomann: ThomannDao, onComplete: (Boolean, Exception?) -> Unit)
    fun updateThomann(thomann: ThomannDao, onComplete: (Boolean, Exception?) -> Unit)
    fun deleteThomann(thomannId: String, userId: String, onComplete: (Boolean, Exception?) -> Unit)
    fun getThomanns(onComplete: (Boolean, List<ThomannDao>?, Exception?) -> Unit)
    fun getThomann(id: String, onComplete: (ThomannDao?, Exception?) -> Unit)
    fun joinThomann(id: String, user: ThomannUserDao, onComplete: (Boolean, Exception?) -> Unit)
    fun leaveThomann(id: String, userId: String, onComplete: (Boolean, Exception?) -> Unit)
    fun isThomannJoinable(thomannId: String, userId: String, onComplete: (Boolean, Boolean?, Exception?) -> Unit)
    fun isThomannAccessible(thomannId: String, userId: String, onComplete: (Boolean, Boolean?, Exception?) -> Unit)
    fun isThomannUpdatable(thomannId: String, userId: String, onComplete: (Boolean, Boolean?, Exception?) -> Unit)
    fun lockThomann(thomannId: String, userId: String, onComplete: (Boolean, Exception?) -> Unit)
    fun unlockThomann(thomannId: String, userId: String, onComplete: (Boolean, Exception?) -> Unit)
}