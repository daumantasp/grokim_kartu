package com.dauma.grokimkartu.general.utils.dispatcher

class DispatcherUtilsImpl: DispatcherUtils {
    private var _isInForeground: Boolean = true
    private val mainDispatcher = MainDispatcher(this)
    private val bgDispatcher = BgDispatcher(this)

    override val isInForeground: Boolean
        get() = _isInForeground

    override val main: Dispatcher
        get() = mainDispatcher

    override val bg: Dispatcher
        get() = bgDispatcher

    override fun appMovedToForeground(isInForeground: Boolean) {
        this._isInForeground = isInForeground
        if (isInForeground) {
            mainDispatcher.movedToForeground()
            bgDispatcher.movedToForeground()
        }
    }
}