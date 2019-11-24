package info.szadkowski.contact.test.configuration

import info.szadkowski.contact.configuration.ScheduleConfiguration
import info.szadkowski.contact.configuration.ThrottlerConfiguration
import info.szadkowski.contact.properties.ThrottlingProperties
import info.szadkowski.contact.throttle.Throttler
import info.szadkowski.contact.throttle.time.SystemTimeProvider
import info.szadkowski.contact.throttle.time.TimeProvider
import org.awaitility.Awaitility
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension
import strikt.api.expectThat
import strikt.assertions.*
import java.time.Duration
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.TimeUnit

@ExtendWith(SpringExtension::class)
class ThrottleConfigurationTest {

    @Nested
    @ContextConfiguration(
        classes = [
            ThrottlerConfiguration::class,
            ScheduleConfiguration::class
        ]
    )
    @TestPropertySource(
        properties = [
            "throttling.clearExpiredRate=24h",
            "throttling.ip.limit=1",
            "throttling.ip.window=24h",
            "throttling.all.limit=2",
            "throttling.all.window=24h"
        ]
    )
    @EnableConfigurationProperties(ThrottlingProperties::class)
    inner class RealImplementation {

        @Test
        fun `Should use SystemTimeProvider`(@Autowired timeProvider: TimeProvider) {
            expectThat(timeProvider).isA<SystemTimeProvider>()
        }

        @Test
        fun `Should create IP Throttler`(@Autowired ipThrottler: Throttler) {
            expectThat(ipThrottler.canProcess("key")).isTrue()
            expectThat(ipThrottler.canProcess("key")).isFalse()
        }

        @Test
        fun `Should create all Throttler`(@Autowired allThrottler: Throttler) {
            expectThat(allThrottler.canProcess("key")).isTrue()
            expectThat(allThrottler.canProcess("key")).isTrue()
            expectThat(allThrottler.canProcess("key")).isFalse()
        }
    }

    @Nested
    @ContextConfiguration(
        classes = [
            ThrottlerConfiguration::class,
            ScheduleConfiguration::class,
            MockingTimeProvider::class
        ]
    )
    @TestPropertySource(
        properties = [
            "throttling.clearExpiredRate=24h",
            "throttling.ip.limit=2",
            "throttling.ip.window=1ms",
            "throttling.all.limit=3",
            "throttling.all.window=2ms"
        ]
    )
    @EnableConfigurationProperties(ThrottlingProperties::class)
    inner class TimeProviderCheck {

        @Autowired
        lateinit var mockedTimeProvider: MockedTimeProvider

        @Test
        fun `Should use IP ThrottlingProperties`(@Autowired ipThrottler: Throttler) {
            mockedTimeProvider.mockedCurrentMillis = 0
            expectThat(ipThrottler.canProcess("key")).isTrue()
            expectThat(ipThrottler.canProcess("key")).isTrue()
            expectThat(ipThrottler.canProcess("key")).isFalse()
            mockedTimeProvider.mockedCurrentMillis = 1
            expectThat(ipThrottler.canProcess("key")).isTrue()
        }

        @Test
        fun `Should use All ThrottlingProperties`(@Autowired allThrottler: Throttler) {
            mockedTimeProvider.mockedCurrentMillis = 0
            expectThat(allThrottler.canProcess("key")).isTrue()
            expectThat(allThrottler.canProcess("key")).isTrue()
            expectThat(allThrottler.canProcess("key")).isTrue()
            expectThat(allThrottler.canProcess("key")).isFalse()
            mockedTimeProvider.mockedCurrentMillis = 1
            expectThat(allThrottler.canProcess("key")).isFalse()
            mockedTimeProvider.mockedCurrentMillis = 2
            expectThat(allThrottler.canProcess("key")).isTrue()
        }
    }

    @Nested
    @ContextConfiguration(
        classes = [
            ThrottlerConfiguration::class,
            ScheduleConfiguration::class,
            MockedThrottlers::class
        ]
    )
    @TestPropertySource(
        properties = [
            "throttling.clearExpiredRate=50ms",
            "throttling.ip.limit=2",
            "throttling.ip.window=1ms",
            "throttling.all.limit=3",
            "throttling.all.window=2ms"
        ]
    )
    @EnableConfigurationProperties(ThrottlingProperties::class)
    inner class ExpiredClearance {

        @Test
        fun `Should clear`(@Autowired throttlers: List<MockedThrottler>) {
            expectThat(throttlers)
                .hasSize(2)
                .all {
                    get { averageTime }.isIn(30.0..70.0)
                }
        }
    }

    @Configuration
    class MockingTimeProvider {

        @Bean
        fun timeProvider(): TimeProvider = MockedTimeProvider()
    }

    @Configuration
    class MockedThrottlers {

        @Bean
        fun ipThrottler(): Throttler = MockedThrottler()

        @Bean
        fun allThrottler(): Throttler = MockedThrottler()
    }

    class MockedTimeProvider : TimeProvider {
        var mockedCurrentMillis: Long = 0
        override val currentMillis: Long
            get() = mockedCurrentMillis
    }

    class MockedThrottler : Throttler {
        private val millisTimes = CopyOnWriteArrayList<Long>()
        val averageTime: Double
            get() {
                Awaitility.await()
                    .atMost(2, TimeUnit.SECONDS)
                    .until { millisTimes.size >= 4 }
                return (1 until 4)
                    .map { millisTimes[it] - millisTimes[it - 1] }
                    .toLongArray()
                    .average()
            }

        override fun canProcess(key: String): Boolean = false

        override fun clearExpired() {
            val nanoTime = System.nanoTime()
            val millisTime = Duration.ofNanos(nanoTime).toMillis()
            millisTimes.add(millisTime)
        }
    }
}
