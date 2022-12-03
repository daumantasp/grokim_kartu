package com.dauma.grokimkartu.repositories.thomanns.paginator

import com.dauma.grokimkartu.data.thomanns.ThomannsDao
import com.dauma.grokimkartu.data.thomanns.entities.ThomannsResponse
import com.dauma.grokimkartu.repositories.thomanns.ThomannsErrors

abstract class ThomannsPaginator(private val thomannsDao: ThomannsDao) {
    protected var _pages: MutableList<ThomannsResponse> = mutableListOf()
    val pages: List<ThomannsResponse>
        get() = _pages

    open val pageSize: Int = 20

    abstract fun loadNextPage(accessToken: String, onComplete: (ThomannsResponse?, ThomannsErrors?) -> Unit)

    fun clear() {
        _pages.clear()
    }

    protected fun isLastLoaded(): Boolean {
        _pages.lastOrNull()?.pageData?.let { pageData ->
            return pageData.currentPage == pageData.lastPage
        }
        return false
    }
}