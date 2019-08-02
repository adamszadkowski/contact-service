package info.szadkowski.contact.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.Resource

@Configuration
@ConfigurationProperties(prefix = "template")
class TemplateProperties {
    lateinit var resource: Resource
}
