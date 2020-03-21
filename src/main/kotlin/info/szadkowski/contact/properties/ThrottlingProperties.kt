package info.szadkowski.contact.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import java.time.Duration

@ConstructorBinding
@ConfigurationProperties(prefix = "throttling")
data class ThrottlingProperties(
    val clearExpiredRate: Duration,
    var ip: ThrottlingScope,
    var all: ThrottlingScope
) {
    data class ThrottlingScope(
        val limit: Long,
        val window: Duration
    )
}
