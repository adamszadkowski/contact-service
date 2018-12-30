package info.szadkowski.contact.configuration;

import info.szadkowski.contact.properties.ThrottlingProperties;
import info.szadkowski.contact.throttle.Throttler;
import info.szadkowski.contact.throttle.ThrottlerFactory;
import info.szadkowski.contact.throttle.properties.ThrottleConfiguration;
import info.szadkowski.contact.throttle.time.SystemTimeProvider;
import info.szadkowski.contact.throttle.time.TimeProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;

import java.util.List;

@Configuration
public class ThrottlerConfiguration {

  @Autowired
  private ThrottlingProperties properties;

  @Bean
  public TimeProvider timeProvider() {
    return new SystemTimeProvider();
  }

  @Bean
  public ThrottlerFactory throttlerFactory(TimeProvider timeProvider) {
    return new ThrottlerFactory(timeProvider);
  }

  @Bean
  public Throttler ipThrottler(ThrottlerFactory throttlerFactory) {
    return createThrottler(throttlerFactory, properties.getIp());
  }

  @Bean
  public Throttler allThrottler(ThrottlerFactory throttlerFactory) {
    return createThrottler(throttlerFactory, properties.getAll());
  }

  @Bean(initMethod = "run")
  public Runnable clearer(TaskScheduler taskScheduler,
                          List<Throttler> throttlers) {
    return () -> {
      for (Throttler throttler : throttlers) {
        taskScheduler.scheduleAtFixedRate(throttler::clearExpired, properties.getClearExpiredRate());
      }
    };
  }

  private Throttler createThrottler(ThrottlerFactory throttlerFactory, ThrottlingProperties.ThrottlingScope scope) {
    return throttlerFactory.create(
            scope.getWindow(),
            ThrottleConfiguration.builder().limit(scope.getLimit()).build()
    );
  }
}
