package info.szadkowski.contact.throttle

import info.szadkowski.contact.throttle.counter.TumblingCounter
import info.szadkowski.contact.throttle.counter.TumblingCounterFactory
import info.szadkowski.contact.throttle.properties.ThrottleConfiguration
import info.szadkowski.contact.throttle.time.TimeProvider
import java.util.concurrent.ConcurrentHashMap

internal class LimitingThrottler(
    private val timeProvider: TimeProvider,
    private val tumblingCounterFactory: TumblingCounterFactory,
    private val throttleConfiguration: ThrottleConfiguration
) : Throttler {
    private val keyToCounterMapping = ConcurrentHashMap<String, TumblingCounter>()

    override fun canProcess(key: String): Boolean {
        val currentMillis = timeProvider.currentMillis
        val count = keyToCounterMapping
            .computeIfAbsent(key) { tumblingCounterFactory() }
            .count(currentMillis)
        return count <= throttleConfiguration.limit
    }

    override fun clearExpired() {
        val currentMillis = timeProvider.currentMillis

        keyToCounterMapping.values
            .removeIf { it.isNewWindow(currentMillis) }
    }
}
