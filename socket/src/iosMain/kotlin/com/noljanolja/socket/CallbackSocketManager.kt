package com.noljanolja.socket

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.*

abstract class CallbackSocketManager {

    fun <T : Any> Flow<T>.asCallbacks() =
        FlowAdapter(MainScope(), this)
}

class FlowAdapter<T : Any>(
    private val scope: CoroutineScope,
    private val flow: Flow<T>,
) {
    fun subscribe(
        onEach: (item: T) -> Unit,
        onComplete: () -> Unit,
        onThrow: (error: Throwable) -> Unit,
    ): Canceller = JobCanceller(
        flow.onEach { onEach(it) }
            .catch { onThrow(it) }
            .onCompletion { onComplete() }
            .launchIn(scope)
    )
}

interface Canceller {
    fun cancel()
}

private class JobCanceller(private val job: Job) : Canceller {
    override fun cancel() {
        job.cancel()
    }
}