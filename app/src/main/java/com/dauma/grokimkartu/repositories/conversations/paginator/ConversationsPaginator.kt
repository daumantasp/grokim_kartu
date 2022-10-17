package com.dauma.grokimkartu.repositories.conversations.paginator

import com.dauma.grokimkartu.data.conversations.entities.MessagesResponse

abstract class ConversationsPaginator {
    protected var _pages: MutableList<MessagesResponse> = mutableListOf()
    val pages: List<MessagesResponse>
        get() = _pages

    open val pageSize: Int = 20

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