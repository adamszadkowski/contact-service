package info.szadkowski.contact.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler

@Configuration
class ScheduleConfiguration {

    @Bean
    fun taskScheduler() = ThreadPoolTaskScheduler().apply {
        poolSize = 1
    }
}
