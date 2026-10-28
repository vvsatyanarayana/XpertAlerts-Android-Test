package com.aquila.pocxpertalerts.data.remote

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import java.util.concurrent.atomic.AtomicBoolean

/** App-wide signal used when any API response indicates that the current session is invalid. */
object SessionExpirationManager {
    private val _events = MutableSharedFlow<Unit>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    val events: SharedFlow<Unit> = _events.asSharedFlow()

    // Prevent concurrent API failures from displaying the same expiration repeatedly.
    private val expirationAlreadySignalled = AtomicBoolean(false)

    fun notifySessionExpired() {
        if (expirationAlreadySignalled.compareAndSet(false, true)) {
            _events.tryEmit(Unit)
        }
    }

    /** Call only after a new login succeeds. */
    fun resetForNewSession() {
        expirationAlreadySignalled.set(false)
    }
}
