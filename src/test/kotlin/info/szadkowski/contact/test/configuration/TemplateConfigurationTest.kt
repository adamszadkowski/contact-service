package info.szadkowski.contact.test.configuration

import info.szadkowski.contact.configuration.TemplateConfiguration
import info.szadkowski.contact.properties.TemplateProperties
import info.szadkowski.contact.template.TemplateFormatter
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension
import strikt.api.expectThat
import strikt.assertions.isEqualTo

@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [TemplateConfiguration::class])
@TestPropertySource(properties = ["template.resource=message.mustache"])
@EnableConfigurationProperties(TemplateProperties::class)
class TemplateConfigurationTest {

    @Autowired
    lateinit var templateFormatter: TemplateFormatter

    @Test
    fun `Should use content field by default`() {
        val context = mapOf("content" to "value")

        val execute = templateFormatter(context)

        expectThat(execute).isEqualTo("value")
    }

    @Test
    fun `Should escape HTML tags`() {
        val context = mapOf("content" to "<div>tag</div>")

        val execute = templateFormatter(context)

        expectThat(execute).isEqualTo("&lt;div&gt;tag&lt;/div&gt;")
    }
}
