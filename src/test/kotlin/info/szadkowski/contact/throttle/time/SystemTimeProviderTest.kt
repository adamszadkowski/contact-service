package info.szadkowski.contact.throttle.time

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isIn

class SystemTimeProviderTest {
    lateinit var timeProvider: TimeProvider

    @BeforeEach
    fun setUp() {
        timeProvider = SystemTimeProvider()
    }

    @Test
    fun `Should provide system time`() = runBlocking<Unit> {
        delay(20)

        val beforeTime = System.currentTimeMillis()
        delay(10)
        val current = timeProvider.currentMillis
        delay(10)
        val afterTime = System.currentTimeMillis()

        expectThat(current).isIn(beforeTime..afterTime)
    }
}
