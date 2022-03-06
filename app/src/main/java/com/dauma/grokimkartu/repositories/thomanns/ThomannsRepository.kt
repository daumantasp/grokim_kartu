package com.dauma.grokimkartu.repositories.thomanns

import com.dauma.grokimkartu.repositories.thomanns.entities.Thomann

interface ThomannsRepository {
    fun getThomanns(onComplete: (Boolean, List<Thomann>?, ThomannsError?) -> Unit)
}