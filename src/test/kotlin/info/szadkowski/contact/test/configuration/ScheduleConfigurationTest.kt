package info.szadkowski.contact.test.configuration

import info.szadkowski.contact.configuration.ScheduleConfiguration
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import strikt.api.expectThat
import strikt.assertions.isEqualTo

@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [ScheduleConfiguration::class])
class ScheduleConfigurationTest {

    @Test
    fun `Should create single thread task scheduler`(@Autowired s: ThreadPoolTaskScheduler) {
        expectThat(s.scheduledThreadPoolExecutor.corePoolSize).isEqualTo(1)
    }
}
