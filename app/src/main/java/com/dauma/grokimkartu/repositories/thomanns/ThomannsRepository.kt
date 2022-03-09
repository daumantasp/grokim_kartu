package com.dauma.grokimkartu.repositories.thomanns

import com.dauma.grokimkartu.repositories.thomanns.entities.Thomann
import java.lang.Exception

interface ThomannsRepository {
    fun getThomanns(onComplete: (Boolean, List<Thomann>?, ThomannsError?) -> Unit)
    fun saveThomann(thomann: Thomann, onComplete: (Boolean, ThomannsError?) -> Unit)
}