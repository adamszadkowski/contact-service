package info.szadkowski.contact.throttle.counter

import org.assertj.core.api.AbstractBooleanAssert
import org.assertj.core.api.AbstractLongAssert
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.Duration

class TumblingCounterTest {
    lateinit var tumblingCounter: TumblingCounter

    @Nested
    inner class ZeroWindow {

        @BeforeEach
        fun setUp() {
            tumblingCounter = TumblingCounter(Duration.ZERO)
        }

        @Test
        fun `Should always create new window`() {
            assertNewWindowWithTimestamp(0).isTrue()
            assertNewWindowWithTimestamp(1).isTrue()
        }

        @Test
        fun `Should always count one`() {
            assertCountWithTimeStamp(0).isEqualTo(1)
            assertCountWithTimeStamp(0).isEqualTo(1)
            assertCountWithTimeStamp(1).isEqualTo(1)
        }
    }

    @Nested
    inner class NonZeroWindow {

        @BeforeEach
        fun setUp() {
            tumblingCounter = TumblingCounter(Duration.ofMillis(10))
        }

        @Test
        fun `Should create new window before first call`() {
            assertNewWindowWithTimestamp(0).isTrue()
            assertNewWindowWithTimestamp(10).isTrue()
        }

        @Nested
        inner class FirstZeroTime {

            @BeforeEach
            fun setUp() {
                tumblingCounter.count(0L)
            }

            @Test
            fun `Should create new window`() {
                assertNewWindowWithTimestamp(10).isTrue()
            }

            @Test
            fun `Should not create new window`() {
                assertNewWindowWithTimestamp(0).isFalse()
                assertNewWindowWithTimestamp(1).isFalse()
                assertNewWindowWithTimestamp(9).isFalse()
            }

            @Test
            fun `Should return one in new window`() {
                assertCountWithTimeStamp(10).isEqualTo(1)
            }

            @Test
            fun `Should return two in same window`() {
                assertCountWithTimeStamp(9).isEqualTo(2)
            }

            @Test
            fun `Should count in past`() {
                assertCountWithTimeStamp(10).isEqualTo(1)
                assertCountWithTimeStamp(9).isEqualTo(2)
            }
        }

        @Nested
        inner class FirstNonZeroTime {

            @BeforeEach
            fun setUp() {
                tumblingCounter.count(5)
            }

            @Test
            fun `Should create new window`() {
                assertNewWindowWithTimestamp(15).isTrue()
            }

            @Test
            fun `Should not create new window`() {
                assertNewWindowWithTimestamp(5).isFalse()
                assertNewWindowWithTimestamp(6).isFalse()
                assertNewWindowWithTimestamp(14).isFalse()
            }

            @Test
            fun `Should return one in new window`() {
                assertCountWithTimeStamp(15).isEqualTo(1)
            }

            @Test
            fun `Should return two in same window`() {
                assertCountWithTimeStamp(14).isEqualTo(2)
            }
        }
    }

    @Test
    fun integration() {
        tumblingCounter = TumblingCounter(Duration.ofMillis(10))

        assertCountWithTimeStamp(1).isEqualTo(1)
        assertCountWithTimeStamp(2).isEqualTo(2)
        assertCountWithTimeStamp(4).isEqualTo(3)
        assertCountWithTimeStamp(10).isEqualTo(4)
        assertCountWithTimeStamp(11).isEqualTo(1)
        assertCountWithTimeStamp(10).isEqualTo(2)
        assertCountWithTimeStamp(11).isEqualTo(3)
        assertCountWithTimeStamp(20).isEqualTo(4)
        assertCountWithTimeStamp(22).isEqualTo(1)
    }

    private fun assertCountWithTimeStamp(timeStampMillis: Long): AbstractLongAssert<*> {
        return assertThat(tumblingCounter.count(timeStampMillis))
    }

    private fun assertNewWindowWithTimestamp(timeStampMillis: Long): AbstractBooleanAssert<*> {
        return assertThat(tumblingCounter.isNewWindow(timeStampMillis))
    }
}
