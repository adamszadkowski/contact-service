package info.szadkowski.contact.throttle

import info.szadkowski.contact.throttle.counter.TumblingCounter
import info.szadkowski.contact.throttle.counter.TumblingCounterFactory
import info.szadkowski.contact.throttle.properties.ThrottleConfiguration
import info.szadkowski.contact.throttle.time.TimeProvider
import org.assertj.core.api.AbstractBooleanAssert
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.Duration

class ThrottlerTest {
    lateinit var throttler: Throttler

    private var systemTimeMillis: Long = 0

    @BeforeEach
    fun setUp() {
        systemTimeMillis = 0
    }

    @Nested
    inner class NoLimit {

        @BeforeEach
        fun setUp() {
            throttler = ThrottlerFactory { systemTimeMillis }
                .create(Duration.ofMillis(2), ThrottleConfiguration.builder().limit(0).build())
        }

        @Test
        fun `Should not throttle`() {
            assertCanProcess("key", 0).isTrue()
        }
    }

    @Nested
    inner class SingleLimitInTwoMillisWindow {

        @BeforeEach
        fun setUp() {
            throttler = ThrottlerFactory { systemTimeMillis }
                .create(Duration.ofMillis(2), ThrottleConfiguration.builder().limit(1).build())
        }

        @Test
        fun `Should process one`() {
            assertCanProcess("key", 0).isTrue()
            assertCanProcess("key", 1).isFalse()
        }

        @Test
        fun `Should process when window tumble`() {
            assertCanProcess("key", 0).isTrue()
            assertCanProcess("key", 1).isFalse()

            assertCanProcess("key", 2).isTrue()
        }

        @Test
        fun `Should process keys separately`() {
            assertCanProcess("key1", 0).isTrue()
            assertCanProcess("key1", 1).isFalse()
            assertCanProcess("key2", 1).isTrue()

            assertCanProcess("key1", 2).isTrue()
            assertCanProcess("key2", 2).isFalse()
        }
    }

    @Nested
    inner class Clearing : TumblingCounterFactory {
        private var creationCount: Int = 0

        @BeforeEach
        fun setUp() {
            creationCount = 0
            throttler = LimitingThrottler(
                TimeProvider { systemTimeMillis },
                this,
                ThrottleConfiguration.builder().limit(1).build()
            )
        }

        override fun create(): TumblingCounter {
            creationCount++
            return TumblingCounter(Duration.ofMillis(10))
        }

        @Test
        fun `Should clear expired window`() {
            // given
            systemTimeMillis = 0
            throttler.canProcess("key")

            // when
            systemTimeMillis = 10
            throttler.clearExpired()

            // then
            assertThat(creationCount).isEqualTo(1)
            throttler.canProcess("key")
            assertThat(creationCount).isEqualTo(2)
        }

        @Test
        fun `Should not clear active window`() {
            // given
            systemTimeMillis = 0
            throttler.canProcess("key")

            // when
            systemTimeMillis = 9
            throttler.clearExpired()

            // then
            assertThat(creationCount).isEqualTo(1)
            throttler.canProcess("key")
            assertThat(creationCount).isEqualTo(1)
        }

        @Test
        fun `Should clear only expired`() {
            // given
            systemTimeMillis = 0
            throttler.canProcess("key1")
            systemTimeMillis = 1
            throttler.canProcess("key2")

            // when
            systemTimeMillis = 10
            throttler.clearExpired()

            // then
            assertThat(creationCount).isEqualTo(2)
            throttler.canProcess("key1")
            assertThat(creationCount).isEqualTo(3)
            throttler.canProcess("key2")
            assertThat(creationCount).isEqualTo(3)
        }
    }

    private fun assertCanProcess(key: String, currentTimeMillis: Long): AbstractBooleanAssert<*> {
        systemTimeMillis = currentTimeMillis
        return assertThat(throttler.canProcess(key))
    }
}
