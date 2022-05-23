package com.dauma.grokimkartu.repositories.thomanns

import com.dauma.grokimkartu.repositories.thomanns.entities.Thomann
import com.dauma.grokimkartu.repositories.thomanns.entities.ThomannDetails

interface ThomannsRepository {
    fun create(thomann: Thomann, onComplete: (ThomannDetails?, ThomannsErrors?) -> Unit)
    fun update(thomannId: Int, thomann: Thomann, onComplete: (ThomannDetails?, ThomannsErrors?) -> Unit)
    fun delete(thomannId: Int, onComplete: (ThomannsErrors?) -> Unit)
    fun thomanns(onComplete: (List<Thomann>?, ThomannsErrors?) -> Unit)
    fun thomannDetails(thomannId: Int, onComplete: (ThomannDetails?, ThomannsErrors?) -> Unit)
    fun join(thomannId: Int, amount: Double, onComplete: (ThomannDetails?, ThomannsErrors?) -> Unit)
    fun quit(thomannId: Int, onComplete: (ThomannDetails?, ThomannsErrors?) -> Unit)
    fun kick(thomannId: Int, userToKickId: Int, onComplete: (ThomannDetails?, ThomannsErrors?) -> Unit)
}