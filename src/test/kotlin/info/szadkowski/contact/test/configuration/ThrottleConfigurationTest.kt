package info.szadkowski.contact.test.configuration

import info.szadkowski.contact.configuration.ScheduleConfiguration
import info.szadkowski.contact.configuration.ThrottlerConfiguration
import info.szadkowski.contact.properties.ThrottlingProperties
import info.szadkowski.contact.throttle.Throttler
import info.szadkowski.contact.throttle.time.SystemTimeProvider
import info.szadkowski.contact.throttle.time.TimeProvider
import org.assertj.core.api.Assertions.assertThat
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
            assertThat(timeProvider).isExactlyInstanceOf(SystemTimeProvider::class.java)
        }

        @Test
        fun `Should create IP Throttler`(@Autowired ipThrottler: Throttler) {
            assertThat(ipThrottler).isNotNull()
            assertThat(ipThrottler.canProcess("key")).isTrue()
            assertThat(ipThrottler.canProcess("key")).isFalse()
        }

        @Test
        fun `Should create all Throttler`(@Autowired allThrottler: Throttler) {
            assertThat(allThrottler).isNotNull()
            assertThat(allThrottler.canProcess("key")).isTrue()
            assertThat(allThrottler.canProcess("key")).isTrue()
            assertThat(allThrottler.canProcess("key")).isFalse()
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
            assertThat(ipThrottler.canProcess("key")).isTrue()
            assertThat(ipThrottler.canProcess("key")).isTrue()
            assertThat(ipThrottler.canProcess("key")).isFalse()
            mockedTimeProvider.mockedCurrentMillis = 1
            assertThat(ipThrottler.canProcess("key")).isTrue()
        }

        @Test
        fun `Should use All ThrottlingProperties`(@Autowired allThrottler: Throttler) {
            mockedTimeProvider.mockedCurrentMillis = 0
            assertThat(allThrottler.canProcess("key")).isTrue()
            assertThat(allThrottler.canProcess("key")).isTrue()
            assertThat(allThrottler.canProcess("key")).isTrue()
            assertThat(allThrottler.canProcess("key")).isFalse()
            mockedTimeProvider.mockedCurrentMillis = 1
            assertThat(allThrottler.canProcess("key")).isFalse()
            mockedTimeProvider.mockedCurrentMillis = 2
            assertThat(allThrottler.canProcess("key")).isTrue()
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
            assertThat(throttlers)
                .hasSize(2)
                .allSatisfy { t ->
                    val times = t.millisTimes
                    Awaitility.await()
                        .atMost(2, TimeUnit.SECONDS)
                        .until { times.size >= 4 }

                    val average = (1 until 4)
                        .map { times[it] - times[it - 1] }
                        .toLongArray()
                        .average()

                    assertThat(average).isBetween(30.0, 70.0)
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
        val millisTimes = CopyOnWriteArrayList<Long>()

        override fun canProcess(key: String): Boolean = false

        override fun clearExpired() {
            val nanoTime = System.nanoTime()
            val millisTime = Duration.ofNanos(nanoTime).toMillis()
            millisTimes.add(millisTime)
        }
    }
}
