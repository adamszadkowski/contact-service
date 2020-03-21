package info.szadkowski.contact.properties

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean
import org.springframework.boot.autoconfigure.mail.MailProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer
import org.springframework.core.io.ClassPathResource
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import strikt.api.expectThat
import strikt.assertions.hasEntry
import strikt.assertions.hasSize
import strikt.assertions.isEmpty
import strikt.assertions.isEqualTo
import java.time.Duration

@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [DefaultConfigurationTest.YamlConfiguration::class])
@EnableConfigurationProperties(
    MailAddressesProperties::class,
    MailProperties::class,
    ThrottlingProperties::class,
    TemplateProperties::class
)
class DefaultConfigurationTest {

    @Test
    fun `Should inject addresses`(@Autowired p: MailAddressesProperties) {
        expectThat(p) {
            get { recipientMail }.isEmpty()
            get { senderMail }.isEmpty()
        }
    }

    @Test
    fun `Should inject throttling`(@Autowired p: ThrottlingProperties) {
        expectThat(p) {
            get { clearExpiredRate }.isEqualTo(Duration.ofHours(24))
            get { ip }
                .and {
                    get { limit }.isEqualTo(5)
                    get { window }.isEqualTo(Duration.ofHours(24))
                }
            get { all }
                .and {
                    get { limit }.isEqualTo(15)
                    get { window }.isEqualTo(Duration.ofHours(24))
                }
        }
    }

    @Test
    fun `Should configure template resource`(@Autowired p: TemplateProperties) {
        expectThat(p.resource) {
            get { filename }.isEqualTo("message.mustache")
            get { inputStream.bufferedReader().use { it.readText() } }.isEqualTo("{{content}}")
        }
    }

    @Test
    fun `Should configure mail starter`(@Autowired p: MailProperties) {
        expectThat(p) {
            get { host }.isEmpty()
            get { port }.isEqualTo(587)
            get { username }.isEmpty()
            get { password }.isEmpty()
            get { properties }
                .hasSize(4)
                .hasEntry("mail.smtp.auth", "true")
                .hasEntry("mail.smtp.starttls.enable", "true")
                .hasEntry("mail.smtp.starttls.required", "true")
                .hasEntry("mail.smtp.ssl.trust", "")
        }
    }

    @Test
    fun `Should setup server port`(@Value("\${server.port}") serverPort: Int) {
        expectThat(serverPort).isEqualTo(80)
    }

    @Configuration
    class YamlConfiguration {

        @Bean
        fun configurer(): PropertySourcesPlaceholderConfigurer {
            val configurer = PropertySourcesPlaceholderConfigurer()
            val yaml = YamlPropertiesFactoryBean()
            yaml.setResources(ClassPathResource("application.yml"))
            yaml.getObject()?.let(configurer::setProperties)
            return configurer
        }
    }
}
