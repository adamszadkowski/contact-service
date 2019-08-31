package info.szadkowski.contact.throttle.time

import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runBlockingTest
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
    fun `Should provide system time`() = runBlockingTest {
        delay(20)

        val beforeTime = System.currentTimeMillis()
        delay(10)
        val current = timeProvider.currentMillis
        delay(10)
        val afterTime = System.currentTimeMillis()

        assertThat(current).isBetween(beforeTime, afterTime)
    }
}
