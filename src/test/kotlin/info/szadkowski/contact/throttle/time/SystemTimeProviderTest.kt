package info.szadkowski.contact.throttle.time

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isIn
import kotlin.time.ExperimentalTime
import kotlin.time.nanoseconds

class SystemTimeProviderTest {
    lateinit var timeProvider: TimeProvider

    @BeforeEach
    fun setUp() {
        timeProvider = SystemTimeProvider()
    }

    @Test
    @ExperimentalTime
    fun `Should provide system time`() = runBlocking<Unit> {
        val beforeTime = System.nanoTime().nanoseconds.toLongMilliseconds()
        val current = timeProvider.currentMillis
        val afterTime = System.nanoTime().nanoseconds.toLongMilliseconds()

        expectThat(current).isIn(beforeTime..afterTime)
    }
}
