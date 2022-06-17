package com.dauma.grokimkartu.general.utils.dispatcher

import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import java.util.*
import kotlin.math.roundToLong

open class DispatcherImpl(
    private val dispatcherUtils: DispatcherUtils,
    private val name: String
) : Dispatcher {
    private val operations: MutableMap<String, Timer> = mutableMapOf()
    private val queued: MutableList<QueuedTask> = mutableListOf()

    protected open lateinit var handler: Handler

    fun movedToForeground() {
        val iterator = queued.iterator()
        while (iterator.hasNext()) {
            val task = iterator.next()
            iterator.remove()
            if (task.isPeriodic == false) {
                asyncNow { task.completion }
            } else {
                periodic(
                    operationKey = task.operationKey,
                    period = task.period,
                    startImmediately = task.startImmediately,
                    repeats = task.repeats,
                    completion = task.completion
                )
            }
        }
    }

    override fun asyncNow(completion: () -> Unit) {
        handler.post {
            if (dispatcherUtils.isInForeground) {
                completion.invoke()
            } else {
                queued.add(
                    QueuedTask(
                        completion = completion,
                        isPeriodic = false
                ))
            }
        }
    }

    override fun asyncAfterSeconds(seconds: Double, completion: () -> Unit) {
        val millis = (seconds * 1000L).roundToLong()
        handler.postDelayed({
            if (dispatcherUtils.isInForeground) {
                completion.invoke()
            } else {
                queued.add(
                    QueuedTask(
                        completion = completion,
                        isPeriodic = false
                    ))
            }
        }, millis)
    }

    override fun periodic(
        operationKey: String,
        period: Double,
        startImmediately: Boolean,
        repeats: Boolean,
        completion: () -> Unit
    ) {
        if (operations.containsKey(operationKey)) {
            return
        }

        val timer = Timer("${this.name}_PERIODIC_TIMER[${operationKey}]")
        operations[operationKey] = timer
        val runnableCompletion = Runnable(completion)
        val timerTask = object : TimerTask() {
            override fun run() {
                if (dispatcherUtils.isInForeground) {
                    handler.post(runnableCompletion)
                } else {
                    cancelPeriodic(operationKey)
                    queued.add(QueuedTask(
                        completion = completion,
                        isPeriodic = false
                    ))
                    if (repeats) {
                        queued.add(
                            QueuedTask(
                                completion = completion,
                                isPeriodic = true,
                                operationKey = operationKey,
                                period = period,
                                startImmediately = repeats
                            )
                        )
                    }
                }

                if (repeats == false) {
                    this.cancel()
                    timer.cancel()
                    timer.purge()
                }
            }
        }

        val periodInMillis = (period * 1000L).roundToLong()
        if (repeats == true) {
            timer.schedule(timerTask, periodInMillis, periodInMillis)
        } else {
            timer.schedule(timerTask, periodInMillis)
        }

        if (startImmediately) {
            asyncNow { completion() }
        }
    }

    override fun cancelPeriodic(key: String) {
        operations[key]?.let { timer ->
            timer.cancel()
            timer.purge()
        }
        operations.remove(key)
    }
}

class MainDispatcher(dispatcherUtils: DispatcherUtils)
    : DispatcherImpl(dispatcherUtils, "MainDispatcher") {
    override var handler: Handler = Handler(Looper.getMainLooper())
}

class BgDispatcher(dispatcherUtils: DispatcherUtils)
    : DispatcherImpl(dispatcherUtils, "BgDispatcher") {
        private var handlerThread: HandlerThread

        init {
            handlerThread = HandlerThread("BG_HANDLER_THREAD")
            handlerThread.start()
            handler = Handler(handlerThread.looper)
        }
}

private data class QueuedTask(
    val completion: () -> Unit = {},
    val isPeriodic: Boolean = false,
    val operationKey: String = "",
    val period: Double = 0.0,
    val startImmediately: Boolean = false,
    val repeats: Boolean = false
)