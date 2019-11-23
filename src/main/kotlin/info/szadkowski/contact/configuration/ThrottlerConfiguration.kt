package info.szadkowski.contact.configuration

import info.szadkowski.contact.properties.ThrottlingProperties
import info.szadkowski.contact.throttle.Throttler
import info.szadkowski.contact.throttle.ThrottlerFactory
import info.szadkowski.contact.throttle.properties.ThrottleConfiguration
import info.szadkowski.contact.throttle.time.SystemTimeProvider
import info.szadkowski.contact.throttle.time.TimeProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.TaskScheduler

@Configuration
class ThrottlerConfiguration {

    @Autowired
    lateinit var properties: ThrottlingProperties

    @Bean
    fun timeProvider() = SystemTimeProvider()

    @Bean
    fun throttlerFactory(timeProvider: TimeProvider) = ThrottlerFactory(timeProvider)

    @Bean
    fun ipThrottler(throttlerFactory: ThrottlerFactory) = createThrottler(throttlerFactory, properties.ip!!)

    @Bean
    fun allThrottler(throttlerFactory: ThrottlerFactory) = createThrottler(throttlerFactory, properties.all!!)

    @Bean(initMethod = "run")
    fun clearer(taskScheduler: TaskScheduler, throttlers: List<Throttler>) = Runnable {
        for (throttler in throttlers) {
            taskScheduler.scheduleAtFixedRate(throttler::clearExpired, properties.clearExpiredRate)
        }
    }

    private fun createThrottler(throttlerFactory: ThrottlerFactory, scope: ThrottlingProperties.ThrottlingScope) =
        throttlerFactory.create(
            scope.window,
            ThrottleConfiguration(limit = scope.limit)
        )
}
