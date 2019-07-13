package info.szadkowski.contact.throttle.time

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SystemTimeProviderTest {
    lateinit var timeProvider: TimeProvider

    @BeforeEach
    fun setUp() {
        timeProvider = SystemTimeProvider()
    }

    @Test
    fun `Should provide system time`() {
        val beforeTime = System.currentTimeMillis()
        val current = timeProvider.currentMillis
        val afterTime = System.currentTimeMillis()

        assertThat(current).isBetween(beforeTime, afterTime)
    }
}
