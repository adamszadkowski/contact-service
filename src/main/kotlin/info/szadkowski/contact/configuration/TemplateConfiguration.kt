package info.szadkowski.contact.configuration

import com.samskivert.mustache.Mustache
import com.samskivert.mustache.Template
import info.szadkowski.contact.properties.TemplateProperties
import info.szadkowski.contact.template.TemplateFormatter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.InputStreamReader
import java.io.Reader

@Configuration
class TemplateConfiguration {

    @Bean
    fun templateReader(@Autowired properties: TemplateProperties) = InputStreamReader(properties.resource.inputStream)

    @Bean
    fun template(reader: Reader) = Mustache.compiler().compile(reader)

    @Bean
    fun templateFormatter(template: Template): TemplateFormatter = { template.execute(this) }
}
