package info.szadkowski.contact.throttle.time

import kotlin.time.ExperimentalTime
import kotlin.time.nanoseconds

class SystemTimeProvider : TimeProvider {
    @ExperimentalTime
    override val currentMillis
        get() = System.nanoTime().nanoseconds.toLongMilliseconds()
}
