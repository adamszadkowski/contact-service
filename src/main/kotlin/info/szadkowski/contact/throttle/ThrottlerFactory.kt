package info.szadkowski.contact.throttle

import info.szadkowski.contact.throttle.counter.TumblingCounter
import info.szadkowski.contact.throttle.counter.TumblingCounterFactory
import info.szadkowski.contact.throttle.properties.ThrottleConfiguration
import info.szadkowski.contact.throttle.time.TimeProvider
import java.time.Duration

class ThrottlerFactory(private val timeProvider: TimeProvider) {

    fun create(windowSize: Duration, throttleConfiguration: ThrottleConfiguration): Throttler {
        return when {
            throttleConfiguration.limit == 0L -> NonLimitingThrottler()
            else -> LimitingThrottler(
                timeProvider,
                object : TumblingCounterFactory {
                    override fun create(): TumblingCounter {
                        return TumblingCounter(windowSize)
                    }
                },
                throttleConfiguration
            )
        }

    }
}
