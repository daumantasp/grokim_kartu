package com.dauma.grokimkartu.repositories.thomanns

import android.graphics.Bitmap
import com.dauma.grokimkartu.repositories.thomanns.entities.Thomann

interface ThomannsRepository {
    fun getThomanns(onComplete: (Boolean, List<Thomann>?, ThomannsError?) -> Unit)
    fun getThomann(id: String, onComplete: (Thomann?, ThomannsError?) -> Unit)
    fun saveThomann(thomann: Thomann, onComplete: (Boolean, ThomannsError?) -> Unit)
    fun isJoinPossible(thomann: Thomann): Boolean
    fun hasAlreadyJoined(thomann: Thomann): Boolean
    fun join(id: String, amount: Double, onComplete: (Boolean, ThomannsError?) -> Unit)
}