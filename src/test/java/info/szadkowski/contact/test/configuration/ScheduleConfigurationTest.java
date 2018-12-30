package info.szadkowski.contact.test.configuration;

import info.szadkowski.contact.configuration.ScheduleConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = ScheduleConfiguration.class)
class ScheduleConfigurationTest {

  @Test
  void shouldCreateSingleThreadTaskScheduler(@Autowired ThreadPoolTaskScheduler taskScheduler) {
    assertThat(taskScheduler.getScheduledThreadPoolExecutor().getCorePoolSize()).isEqualTo(1);
  }
}
