package info.szadkowski.contact.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import java.time.Duration

@Configuration
@ConfigurationProperties(prefix = "throttling")
class ThrottlingProperties {
    lateinit var clearExpiredRate: Duration
    var ip: ThrottlingScope? = null
    var all: ThrottlingScope? = null

    class ThrottlingScope {
        var limit: Long = 0
        lateinit var window: Duration
    }
}
