package info.szadkowski.contact.throttle.counter

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import strikt.api.DescribeableBuilder
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isFalse
import strikt.assertions.isTrue
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
            expectNewWindowWithTimestamp(0).isTrue()
            expectNewWindowWithTimestamp(1).isTrue()
        }

        @Test
        fun `Should always count one`() {
            expectCountWithTimeStamp(0).isEqualTo(1)
            expectCountWithTimeStamp(0).isEqualTo(1)
            expectCountWithTimeStamp(1).isEqualTo(1)
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
            expectNewWindowWithTimestamp(0).isTrue()
            expectNewWindowWithTimestamp(10).isTrue()
        }

        @Nested
        inner class FirstZeroTime {

            @BeforeEach
            fun setUp() {
                tumblingCounter.count(0L)
            }

            @Test
            fun `Should create new window`() {
                expectNewWindowWithTimestamp(10).isTrue()
            }

            @Test
            fun `Should not create new window`() {
                expectNewWindowWithTimestamp(0).isFalse()
                expectNewWindowWithTimestamp(1).isFalse()
                expectNewWindowWithTimestamp(9).isFalse()
            }

            @Test
            fun `Should return one in new window`() {
                expectCountWithTimeStamp(10).isEqualTo(1)
            }

            @Test
            fun `Should return two in same window`() {
                expectCountWithTimeStamp(9).isEqualTo(2)
            }

            @Test
            fun `Should count in past`() {
                expectCountWithTimeStamp(10).isEqualTo(1)
                expectCountWithTimeStamp(9).isEqualTo(2)
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
                expectNewWindowWithTimestamp(15).isTrue()
            }

            @Test
            fun `Should not create new window`() {
                expectNewWindowWithTimestamp(5).isFalse()
                expectNewWindowWithTimestamp(6).isFalse()
                expectNewWindowWithTimestamp(14).isFalse()
            }

            @Test
            fun `Should return one in new window`() {
                expectCountWithTimeStamp(15).isEqualTo(1)
            }

            @Test
            fun `Should return two in same window`() {
                expectCountWithTimeStamp(14).isEqualTo(2)
            }
        }
    }

    @Test
    fun integration() {
        tumblingCounter = TumblingCounter(Duration.ofMillis(10))

        expectCountWithTimeStamp(1).isEqualTo(1)
        expectCountWithTimeStamp(2).isEqualTo(2)
        expectCountWithTimeStamp(4).isEqualTo(3)
        expectCountWithTimeStamp(10).isEqualTo(4)
        expectCountWithTimeStamp(11).isEqualTo(1)
        expectCountWithTimeStamp(10).isEqualTo(2)
        expectCountWithTimeStamp(11).isEqualTo(3)
        expectCountWithTimeStamp(20).isEqualTo(4)
        expectCountWithTimeStamp(22).isEqualTo(1)
    }

    private fun expectCountWithTimeStamp(timeStampMillis: Long): DescribeableBuilder<Long> {
        return expectThat(tumblingCounter.count(timeStampMillis))
    }

    private fun expectNewWindowWithTimestamp(timeStampMillis: Long): DescribeableBuilder<Boolean> {
        return expectThat(tumblingCounter.isNewWindow(timeStampMillis))
    }
}
