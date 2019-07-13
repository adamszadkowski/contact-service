package info.szadkowski.contact.properties

import org.assertj.core.api.Assertions.assertThat
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
        assertThat(p.recipientMail).isEmpty()
        assertThat(p.senderMail).isEmpty()
    }

    @Test
    fun `Should inject throttling`(@Autowired p: ThrottlingProperties) {
        assertThat(p.clearExpiredRate).isEqualTo(Duration.ofHours(24))
        assertThat(p.ip.limit).isEqualTo(5)
        assertThat(p.ip.window).isEqualTo(Duration.ofHours(24))
        assertThat(p.all.limit).isEqualTo(15)
        assertThat(p.all.window).isEqualTo(Duration.ofHours(24))
    }

    @Test
    fun `Should configure template resource`(@Autowired p: TemplateProperties) {
        val resource = p.resource
        assertThat(resource.filename).isEqualTo("message.mustache")
        assertThat(resource.inputStream).hasContent("{{content}}")
    }

    @Test
    fun `Should configure mail starter`(@Autowired p: MailProperties) {
        assertThat(p.host).isEmpty()
        assertThat(p.port).isEqualTo(587)
        assertThat(p.username).isEmpty()
        assertThat(p.password).isEmpty()
        assertThat(p.properties)
            .hasSize(4)
            .containsEntry("mail.smtp.auth", "true")
            .containsEntry("mail.smtp.starttls.enable", "true")
            .containsEntry("mail.smtp.starttls.required", "true")
            .containsEntry("mail.smtp.ssl.trust", "")
    }

    @Test
    fun `Should setup server port`(@Value("\${server.port}") serverPort: Int) {
        assertThat(serverPort).isEqualTo(80)
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
