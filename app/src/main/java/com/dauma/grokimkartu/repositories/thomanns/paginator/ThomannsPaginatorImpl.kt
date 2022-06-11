package com.dauma.grokimkartu.repositories.thomanns.paginator

import com.dauma.grokimkartu.data.thomanns.ThomannsDao
import com.dauma.grokimkartu.data.thomanns.entities.ThomannsResponse
import com.dauma.grokimkartu.repositories.thomanns.ThomannsErrors

class ThomannsPaginatorImpl(private val thomannsDao: ThomannsDao) : ThomannsPaginator {
    private var _pages: MutableList<ThomannsResponse> = mutableListOf()
    override val pages: List<ThomannsResponse>
        get() = _pages
    override val pageSize: Int = 5

    override fun loadNextPage(accessToken: String, onComplete: (ThomannsResponse?, ThomannsErrors?) -> Unit) {
        if (isLastLoaded() == false) {
            val nextPage = _pages.count() + 1
            thomannsDao.thomanns(nextPage, pageSize, accessToken) { thomannsResponse, thomannsDaoResponseStatus ->
                if (thomannsDaoResponseStatus.isSuccessful && thomannsResponse != null) {
                    _pages.add(thomannsResponse)
                    onComplete(thomannsResponse, null)
                } else {
                    onComplete(null, ThomannsErrors.UNKNOWN)
                }
            }
        } else {
            onComplete(_pages.lastOrNull(), null)
        }

    }

    override fun clear() {
        _pages.clear()
    }

    private fun isLastLoaded(): Boolean {
        _pages.lastOrNull()?.pageData?.let { pageData ->
            return pageData.currentPage == pageData.lastPage
        }
        return false
    }
}