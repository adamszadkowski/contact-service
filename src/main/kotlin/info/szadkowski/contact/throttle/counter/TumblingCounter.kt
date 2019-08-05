package info.szadkowski.contact.throttle.counter

import java.time.Duration

class TumblingCounter(windowSize: Duration) {
    private val lock = Any()
    private val windowSizeMillis: Long = windowSize.toMillis()
    private var windowOpenTimeStampMillis: Long = -1
    private var counter: Long = 0

    private val isFirstWindow: Boolean
        get() = windowOpenTimeStampMillis < 0

    fun count(timeStampMillis: Long): Long {
        synchronized(lock) {
            if (isNewWindow(timeStampMillis)) {
                windowOpenTimeStampMillis = timeStampMillis
                counter = 0
            }

            return ++counter
        }
    }

    fun isNewWindow(timeStampMillis: Long): Boolean {
        synchronized(lock) {
            return isFirstWindow || isTimeStampAfterWindow(timeStampMillis)
        }
    }

    private fun isTimeStampAfterWindow(timeStampMillis: Long) =
        windowOpenTimeStampMillis + windowSizeMillis <= timeStampMillis
}
