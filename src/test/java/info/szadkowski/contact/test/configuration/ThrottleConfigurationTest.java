package info.szadkowski.contact.test.configuration;

import info.szadkowski.contact.configuration.ScheduleConfiguration;
import info.szadkowski.contact.configuration.ThrottlerConfiguration;
import info.szadkowski.contact.properties.ThrottlingProperties;
import info.szadkowski.contact.throttle.Throttler;
import info.szadkowski.contact.throttle.time.SystemTimeProvider;
import info.szadkowski.contact.throttle.time.TimeProvider;
import lombok.Getter;
import lombok.Setter;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
class ThrottleConfigurationTest {

  @Nested
  @ContextConfiguration(classes = {
          ThrottlingProperties.class,
          ThrottlerConfiguration.class,
          ScheduleConfiguration.class
  })
  @TestPropertySource(properties = {
          "throttling.clearExpiredRate=24h",
          "throttling.ip.limit=1",
          "throttling.ip.window=24h",
          "throttling.all.limit=2",
          "throttling.all.window=24h"
  })
  @EnableConfigurationProperties
  class RealImplementation {

    @Test
    void shouldUseSystemTimeProvider(@Autowired TimeProvider timeProvider) {
      assertThat(timeProvider).isExactlyInstanceOf(SystemTimeProvider.class);
    }

    @Test
    void shouldCreateIpThrottler(@Autowired Throttler ipThrottler) {
      assertThat(ipThrottler).isNotNull();
      assertThat(ipThrottler.canProcess("key")).isTrue();
      assertThat(ipThrottler.canProcess("key")).isFalse();
    }

    @Test
    void shouldCreateAllThrottler(@Autowired Throttler allThrottler) {
      assertThat(allThrottler).isNotNull();
      assertThat(allThrottler.canProcess("key")).isTrue();
      assertThat(allThrottler.canProcess("key")).isTrue();
      assertThat(allThrottler.canProcess("key")).isFalse();
    }
  }

  @Nested
  @ContextConfiguration(classes = {
          ThrottlingProperties.class,
          ThrottlerConfiguration.class,
          ScheduleConfiguration.class,
          MockingTimeProvider.class
  })
  @TestPropertySource(properties = {
          "throttling.clearExpiredRate=24h",
          "throttling.ip.limit=2",
          "throttling.ip.window=1ms",
          "throttling.all.limit=3",
          "throttling.all.window=2ms"
  })
  @EnableConfigurationProperties
  class TimeProviderCheck {

    @Autowired
    private MockedTimeProvider mockedTimeProvider;

    @Test
    void shouldUseIpThrottlingProperties(@Autowired Throttler ipThrottler) {
      mockedTimeProvider.setCurrentMillis(0);
      assertThat(ipThrottler.canProcess("key")).isTrue();
      assertThat(ipThrottler.canProcess("key")).isTrue();
      assertThat(ipThrottler.canProcess("key")).isFalse();
      mockedTimeProvider.setCurrentMillis(1);
      assertThat(ipThrottler.canProcess("key")).isTrue();
    }

    @Test
    void shouldUseAllThrottlingProperties(@Autowired Throttler allThrottler) {
      mockedTimeProvider.setCurrentMillis(0);
      assertThat(allThrottler.canProcess("key")).isTrue();
      assertThat(allThrottler.canProcess("key")).isTrue();
      assertThat(allThrottler.canProcess("key")).isTrue();
      assertThat(allThrottler.canProcess("key")).isFalse();
      mockedTimeProvider.setCurrentMillis(1);
      assertThat(allThrottler.canProcess("key")).isFalse();
      mockedTimeProvider.setCurrentMillis(2);
      assertThat(allThrottler.canProcess("key")).isTrue();
    }
  }

  @Nested
  @ContextConfiguration(classes = {
          ThrottlingProperties.class,
          ThrottlerConfiguration.class,
          ScheduleConfiguration.class,
          MockedThrottlers.class
  })
  @TestPropertySource(properties = {
          "throttling.clearExpiredRate=50ms",
          "throttling.ip.limit=2",
          "throttling.ip.window=1ms",
          "throttling.all.limit=3",
          "throttling.all.window=2ms"
  })
  @EnableConfigurationProperties
  class ExpiredClearance {

    @Test
    void shouldClear(@Autowired List<MockedThrottler> throttlers) {
      assertThat(throttlers)
              .hasSize(2)
              .allSatisfy(t -> {
                List<Long> times = t.getMillisTimes();
                Awaitility.await()
                        .atMost(2, TimeUnit.SECONDS)
                        .until(() -> times.size() >= 4);

                double average = IntStream.range(1, 4)
                        .mapToLong(i -> times.get(i) - times.get(i - 1))
                        .summaryStatistics()
                        .getAverage();

                assertThat(average).isBetween(30d, 70d);
              });
    }
  }

  @Configuration
  public static class MockingTimeProvider {

    @Bean
    public TimeProvider timeProvider() {
      return new MockedTimeProvider();
    }
  }

  @Configuration
  public static class MockedThrottlers {

    @Bean
    public Throttler ipThrottler() {
      return new MockedThrottler();
    }

    @Bean
    public Throttler allThrottler() {
      return new MockedThrottler();
    }
  }

  private static class MockedTimeProvider implements TimeProvider {

    @Setter
    private int currentMillis = 0;

    @Override
    public long getCurrentMillis() {
      return currentMillis;
    }
  }

  private static class MockedThrottler implements Throttler {

    @Getter
    private final List<Long> millisTimes = new CopyOnWriteArrayList<>();

    @Override
    public boolean canProcess(String key) {
      return false;
    }

    @Override
    public void clearExpired() {
      long nanoTime = System.nanoTime();
      long millisTime = Duration.ofNanos(nanoTime).toMillis();
      millisTimes.add(millisTime);
    }
  }
}
