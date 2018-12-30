package info.szadkowski.contact.throttle;

import info.szadkowski.contact.throttle.counter.TumblingCounter;
import info.szadkowski.contact.throttle.counter.TumblingCounterFactory;
import info.szadkowski.contact.throttle.properties.ThrottleConfiguration;
import info.szadkowski.contact.throttle.time.TimeProvider;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

class LimitingThrottler implements Throttler {
  private final ConcurrentMap<String, TumblingCounter> keyToCounterMapping = new ConcurrentHashMap<>();

  private final TimeProvider timeProvider;
  private final TumblingCounterFactory tumblingCounterFactory;
  private final ThrottleConfiguration throttleConfiguration;

  LimitingThrottler(TimeProvider timeProvider,
                    TumblingCounterFactory tumblingCounterFactory,
                    ThrottleConfiguration throttleConfiguration) {
    this.timeProvider = timeProvider;
    this.tumblingCounterFactory = tumblingCounterFactory;
    this.throttleConfiguration = throttleConfiguration;
  }

  @Override
  public boolean canProcess(String key) {
    long currentMillis = timeProvider.getCurrentMillis();
    long count = keyToCounterMapping
            .computeIfAbsent(key, k -> tumblingCounterFactory.create())
            .count(currentMillis);
    return count <= throttleConfiguration.getLimit();
  }

  @Override
  public void clearExpired() {
    long currentMillis = timeProvider.getCurrentMillis();

    keyToCounterMapping.values()
            .removeIf(c -> c.isNewWindow(currentMillis));
  }
}
