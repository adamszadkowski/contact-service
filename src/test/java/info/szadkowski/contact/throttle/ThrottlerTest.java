package info.szadkowski.contact.throttle;

import info.szadkowski.contact.throttle.counter.TumblingCounter;
import info.szadkowski.contact.throttle.counter.TumblingCounterFactory;
import info.szadkowski.contact.throttle.properties.ThrottleConfiguration;
import org.assertj.core.api.AbstractBooleanAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

class ThrottlerTest {
  private Throttler throttler;

  private long systemTimeMillis;

  @BeforeEach
  void setUp() {
    systemTimeMillis = 0;
  }

  @Nested
  class NoLimit {

    @BeforeEach
    void setUp() {
      throttler = new ThrottlerFactory(() -> systemTimeMillis)
              .create(Duration.ofMillis(2), ThrottleConfiguration.builder().limit(0).build());
    }

    @Test
    void shouldNotThrottle() {
      assertCanProcess("key", 0).isTrue();
    }
  }

  @Nested
  class SingleLimitInTwoMillisWindow {

    @BeforeEach
    void setUp() {
      throttler = new ThrottlerFactory(() -> systemTimeMillis)
              .create(Duration.ofMillis(2), ThrottleConfiguration.builder().limit(1).build());
    }

    @Test
    void shouldProcessOne() {
      assertCanProcess("key", 0).isTrue();
      assertCanProcess("key", 1).isFalse();
    }

    @Test
    void shouldProcessWhenWindowTumble() {
      assertCanProcess("key", 0).isTrue();
      assertCanProcess("key", 1).isFalse();

      assertCanProcess("key", 2).isTrue();
    }

    @Test
    void shouldProcessKeysSeparately() {
      assertCanProcess("key1", 0).isTrue();
      assertCanProcess("key1", 1).isFalse();
      assertCanProcess("key2", 1).isTrue();

      assertCanProcess("key1", 2).isTrue();
      assertCanProcess("key2", 2).isFalse();
    }
  }

  @Nested
  class Clearing implements TumblingCounterFactory {
    private int creationCount;

    @BeforeEach
    void setUp() {
      creationCount = 0;
      throttler = new LimitingThrottler(() -> systemTimeMillis, this, ThrottleConfiguration.builder().limit(1).build());
    }

    @Override
    public TumblingCounter create() {
      creationCount++;
      return new TumblingCounter(Duration.ofMillis(10));
    }

    @Test
    void shouldClearExpiredWindow() {
      // given
      systemTimeMillis = 0;
      throttler.canProcess("key");

      // when
      systemTimeMillis = 10;
      throttler.clearExpired();

      // then
      assertThat(creationCount).isEqualTo(1);
      throttler.canProcess("key");
      assertThat(creationCount).isEqualTo(2);
    }

    @Test
    void shouldNotClearActiveWindow() {
      // given
      systemTimeMillis = 0;
      throttler.canProcess("key");

      // when
      systemTimeMillis = 9;
      throttler.clearExpired();

      // then
      assertThat(creationCount).isEqualTo(1);
      throttler.canProcess("key");
      assertThat(creationCount).isEqualTo(1);
    }

    @Test
    void shouldClearOnlyExpired() {
      // given
      systemTimeMillis = 0;
      throttler.canProcess("key1");
      systemTimeMillis = 1;
      throttler.canProcess("key2");

      // when
      systemTimeMillis = 10;
      throttler.clearExpired();

      // then
      assertThat(creationCount).isEqualTo(2);
      throttler.canProcess("key1");
      assertThat(creationCount).isEqualTo(3);
      throttler.canProcess("key2");
      assertThat(creationCount).isEqualTo(3);
    }
  }

  private AbstractBooleanAssert<?> assertCanProcess(String key, int currentTimeMillis) {
    systemTimeMillis = currentTimeMillis;
    return assertThat(throttler.canProcess(key));
  }
}
