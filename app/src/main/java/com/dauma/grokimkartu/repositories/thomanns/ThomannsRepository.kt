package com.dauma.grokimkartu.repositories.thomanns

import com.dauma.grokimkartu.repositories.thomanns.entities.Thomann

interface ThomannsRepository {
    fun getThomanns(onComplete: (Boolean, List<Thomann>?, ThomannsError?) -> Unit)
    fun getThomann(id: String, onComplete: (Thomann?, ThomannsError?) -> Unit)
    fun saveThomann(thomann: Thomann, onComplete: (Boolean, ThomannsError?) -> Unit)
    fun join(id: String, amount: Double, onComplete: (Boolean, ThomannsError?) -> Unit)
    fun deleteThomann(id: String, onComplete: (Boolean, ThomannsError?) -> Unit)
    fun leaveThomann(id: String, onComplete: (Boolean, ThomannsError?) -> Unit)
    fun isJoinable(id: String, onComplete: (Boolean, Boolean?, ThomannsError?) -> Unit)
    fun isAccessible(id: String, onComplete: (Boolean, Boolean?, ThomannsError?) -> Unit)
    fun isUpdatable(id: String, onComplete: (Boolean, Boolean?, ThomannsError?) -> Unit)
    fun lockThomann(thomannId: String, onComplete: (Boolean, ThomannsError?) -> Unit)
    fun unlockThomann(thomannId: String, onComplete: (Boolean, ThomannsError?) -> Unit)
    fun kickUserFromThomann(thomannId: String, userToKickId: String, onComplete: (Boolean, ThomannsError?) -> Unit)
}