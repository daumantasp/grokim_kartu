package com.dauma.grokimkartu.repositories.thomanns

import com.dauma.grokimkartu.repositories.thomanns.entities.*

interface MyThomannsRepository {
    val pages: List<ThomannsPage>
    fun loadNextPage(onComplete: (ThomannsPage?, ThomannsErrors?) -> Unit)
    fun thomannDetails(thomannId: Int, onComplete: (ThomannDetails?, ThomannsErrors?) -> Unit)
    fun reload(onComplete: (ThomannsPage?, ThomannsErrors?) -> Unit)
}