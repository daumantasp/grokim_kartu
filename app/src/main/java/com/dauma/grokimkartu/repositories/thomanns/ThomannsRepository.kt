package com.dauma.grokimkartu.repositories.thomanns

import com.dauma.grokimkartu.repositories.thomanns.entities.Thomann

interface ThomannsRepository {
    fun getThomanns(onComplete: (Boolean, List<Thomann>?, ThomannsError?) -> Unit)
    fun getThomann(id: String, onComplete: (Thomann?, ThomannsError?) -> Unit)
    fun saveThomann(thomann: Thomann, onComplete: (Boolean, ThomannsError?) -> Unit)
    fun isJoinPossible(thomann: Thomann): Boolean
    fun join(id: String, onComplete: (Thomann?, ThomannsError?) -> Unit)
}