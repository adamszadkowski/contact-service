package info.szadkowski.contact.throttle.time;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SystemTimeProviderTest {
  private TimeProvider timeProvider;

  @BeforeEach
  void setUp() {
    timeProvider = new SystemTimeProvider();
  }

  @Test
  void shouldProvideSystemTime() {
    long beforeTime = System.currentTimeMillis();
    long current = timeProvider.getCurrentMillis();
    long afterTime = System.currentTimeMillis();

    assertThat(current).isBetween(beforeTime, afterTime);
  }
}
