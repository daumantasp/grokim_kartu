package com.dauma.grokimkartu.repositories.thomanns.paginator

import com.dauma.grokimkartu.data.thomanns.entities.ThomannsResponse
import com.dauma.grokimkartu.repositories.thomanns.ThomannsErrors

interface ThomannsPaginator {
    val pages: List<ThomannsResponse>
    val pageSize: Int
    fun loadNextPage(accessToken: String, onComplete: (ThomannsResponse?, ThomannsErrors?) -> Unit)
    fun clear()
}