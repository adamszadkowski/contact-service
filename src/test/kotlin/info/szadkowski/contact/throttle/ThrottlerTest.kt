package info.szadkowski.contact.throttle

import info.szadkowski.contact.throttle.counter.TumblingCounter
import info.szadkowski.contact.throttle.properties.ThrottleConfiguration
import info.szadkowski.contact.throttle.time.TimeProvider
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import strikt.api.DescribeableBuilder
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isFalse
import strikt.assertions.isTrue
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
            throttler = ThrottlerFactory(object : TimeProvider {
                override val currentMillis: Long
                    get() = systemTimeMillis
            }).create(Duration.ofMillis(2), ThrottleConfiguration(limit = 0))
        }

        @Test
        fun `Should not throttle`() {
            expectCanProcess("key", 0).isTrue()
        }
    }

    @Nested
    inner class SingleLimitInTwoMillisWindow {

        @BeforeEach
        fun setUp() {
            throttler = ThrottlerFactory(object : TimeProvider {
                override val currentMillis: Long
                    get() = systemTimeMillis
            }).create(Duration.ofMillis(2), ThrottleConfiguration(limit = 1))
        }

        @Test
        fun `Should process one`() {
            expectCanProcess("key", 0).isTrue()
            expectCanProcess("key", 1).isFalse()
        }

        @Test
        fun `Should process when window tumble`() {
            expectCanProcess("key", 0).isTrue()
            expectCanProcess("key", 1).isFalse()

            expectCanProcess("key", 2).isTrue()
        }

        @Test
        fun `Should process keys separately`() {
            expectCanProcess("key1", 0).isTrue()
            expectCanProcess("key1", 1).isFalse()
            expectCanProcess("key2", 1).isTrue()

            expectCanProcess("key1", 2).isTrue()
            expectCanProcess("key2", 2).isFalse()
        }
    }

    @Nested
    inner class Clearing {
        private var creationCount: Int = 0

        @BeforeEach
        fun setUp() {
            creationCount = 0
            throttler = LimitingThrottler(
                object : TimeProvider {
                    override val currentMillis: Long
                        get() = systemTimeMillis
                },
                {
                    creationCount++
                    TumblingCounter(Duration.ofMillis(10))
                },
                ThrottleConfiguration(limit = 1)
            )
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
            expectThat(creationCount).isEqualTo(1)
            throttler.canProcess("key")
            expectThat(creationCount).isEqualTo(2)
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
            expectThat(creationCount).isEqualTo(1)
            throttler.canProcess("key")
            expectThat(creationCount).isEqualTo(1)
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
            expectThat(creationCount).isEqualTo(2)
            throttler.canProcess("key1")
            expectThat(creationCount).isEqualTo(3)
            throttler.canProcess("key2")
            expectThat(creationCount).isEqualTo(3)
        }
    }

    private fun expectCanProcess(key: String, currentTimeMillis: Long): DescribeableBuilder<Boolean> {
        systemTimeMillis = currentTimeMillis
        return expectThat(throttler.canProcess(key))
    }
}
