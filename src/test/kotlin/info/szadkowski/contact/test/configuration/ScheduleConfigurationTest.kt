package info.szadkowski.contact.test.configuration

import info.szadkowski.contact.configuration.ScheduleConfiguration
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [ScheduleConfiguration::class])
class ScheduleConfigurationTest {

    @Test
    fun `Should create single thread task scheduler`(@Autowired s: ThreadPoolTaskScheduler) {
        assertThat(s.scheduledThreadPoolExecutor.corePoolSize).isEqualTo(1)
    }
}
