package info.szadkowski.contact.configuration;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;
import info.szadkowski.contact.properties.TemplateProperties;
import info.szadkowski.contact.template.TemplateFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

@Configuration
public class TemplateConfiguration {

  @Bean
  public Reader templateReader(@Autowired TemplateProperties properties) throws IOException {
    return new InputStreamReader(properties.getResource().getInputStream());
  }

  @Bean
  public Template template(Reader reader) {
    return Mustache.compiler().compile(reader);
  }

  @Bean
  public TemplateFormatter templateFormatter(Template template) {
    return template::execute;
  }
}
