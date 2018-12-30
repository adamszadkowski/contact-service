package info.szadkowski.contact.throttle.counter;

import org.assertj.core.api.AbstractBooleanAssert;
import org.assertj.core.api.AbstractLongAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

class TumblingCounterTest {
  private TumblingCounter tumblingCounter;

  @Nested
  class ZeroWindow {

    @BeforeEach
    void setUp() {
      tumblingCounter = new TumblingCounter(Duration.ZERO);
    }

    @Test
    void shouldAlwaysCreateNewWindow() {
      assertNewWindowWithTimestamp(0).isTrue();
      assertNewWindowWithTimestamp(1).isTrue();
    }

    @Test
    void shouldAlwaysCountOne() {
      assertCountWithTimeStamp(0).isEqualTo(1);
      assertCountWithTimeStamp(0).isEqualTo(1);
      assertCountWithTimeStamp(1).isEqualTo(1);
    }
  }

  @Nested
  class NonZeroWindow {

    @BeforeEach
    void setUp() {
      tumblingCounter = new TumblingCounter(Duration.ofMillis(10));
    }

    @Test
    void shouldCreateNewWindowBeforeFirstCall() {
      assertNewWindowWithTimestamp(0).isTrue();
      assertNewWindowWithTimestamp(10).isTrue();
    }

    @Nested
    class FirstZeroTime {

      @BeforeEach
      void setUp() {
        tumblingCounter.count(0L);
      }

      @Test
      void shouldCreateNewWindow() {
        assertNewWindowWithTimestamp(10).isTrue();
      }

      @Test
      void shouldNotCreateNewWindow() {
        assertNewWindowWithTimestamp(0).isFalse();
        assertNewWindowWithTimestamp(1).isFalse();
        assertNewWindowWithTimestamp(9).isFalse();
      }

      @Test
      void shouldReturnOneInNewWindow() {
        assertCountWithTimeStamp(10).isEqualTo(1);
      }

      @Test
      void shouldReturnTwoInSameWindow() {
        assertCountWithTimeStamp(9).isEqualTo(2);
      }

      @Test
      void shouldCountInPast() {
        assertCountWithTimeStamp(10).isEqualTo(1);
        assertCountWithTimeStamp(9).isEqualTo(2);
      }
    }

    @Nested
    class FirstNonZeroTime {

      @BeforeEach
      void setUp() {
        tumblingCounter.count(5);
      }

      @Test
      void shouldCreateNewWindow() {
        assertNewWindowWithTimestamp(15).isTrue();
      }

      @Test
      void shouldNotCreateNewWindow() {
        assertNewWindowWithTimestamp(5).isFalse();
        assertNewWindowWithTimestamp(6).isFalse();
        assertNewWindowWithTimestamp(14).isFalse();
      }

      @Test
      void shouldReturnOneInNewWindow() {
        assertCountWithTimeStamp(15).isEqualTo(1);
      }

      @Test
      void shouldReturnTwoInSameWindow() {
        assertCountWithTimeStamp(14).isEqualTo(2);
      }
    }
  }

  @Test
  void integration() {
    tumblingCounter = new TumblingCounter(Duration.ofMillis(10));

    assertCountWithTimeStamp(1).isEqualTo(1);
    assertCountWithTimeStamp(2).isEqualTo(2);
    assertCountWithTimeStamp(4).isEqualTo(3);
    assertCountWithTimeStamp(10).isEqualTo(4);
    assertCountWithTimeStamp(11).isEqualTo(1);
    assertCountWithTimeStamp(10).isEqualTo(2);
    assertCountWithTimeStamp(11).isEqualTo(3);
    assertCountWithTimeStamp(20).isEqualTo(4);
    assertCountWithTimeStamp(22).isEqualTo(1);
  }

  private AbstractLongAssert<?> assertCountWithTimeStamp(long timeStampMillis) {
    return assertThat(tumblingCounter.count(timeStampMillis));
  }

  private AbstractBooleanAssert<?> assertNewWindowWithTimestamp(long timeStampMillis) {
    return assertThat(tumblingCounter.isNewWindow(timeStampMillis));
  }
}
