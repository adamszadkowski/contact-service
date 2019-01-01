package info.szadkowski.contact.test.configuration;

import info.szadkowski.contact.configuration.TemplateConfiguration;
import info.szadkowski.contact.properties.TemplateProperties;
import info.szadkowski.contact.template.TemplateFormatter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        TemplateProperties.class,
        TemplateConfiguration.class
})
@TestPropertySource(properties = {
        "template.resource=message.mustache"
})
@EnableConfigurationProperties
class TemplateConfigurationTest {

  @Test
  void shouldCreateTemplateFormatterBean(@Autowired TemplateFormatter templateFormatter) {
    var context = Collections.singletonMap("content", "value");

    String execute = templateFormatter.format(context);

    assertThat(execute).isEqualTo("value");
  }
}
