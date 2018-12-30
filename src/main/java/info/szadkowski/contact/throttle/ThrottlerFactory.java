package info.szadkowski.contact.throttle;

import info.szadkowski.contact.throttle.counter.TumblingCounter;
import info.szadkowski.contact.throttle.properties.ThrottleConfiguration;
import info.szadkowski.contact.throttle.time.TimeProvider;

import java.time.Duration;

public class ThrottlerFactory {
  private final TimeProvider timeProvider;

  public ThrottlerFactory(TimeProvider timeProvider) {
    this.timeProvider = timeProvider;
  }

  public Throttler create(Duration windowSize, ThrottleConfiguration throttleConfiguration) {
    if (throttleConfiguration.getLimit() == 0)
      return new NonLimitingThrottler();

    return new LimitingThrottler(
            timeProvider,
            () -> new TumblingCounter(windowSize),
            throttleConfiguration);
  }
}
