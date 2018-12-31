package info.szadkowski.contact.properties;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
class ApplicationEnvironmentConfigurationTest {

  @Nested
  @ContextConfiguration(classes = {
          MailAddressesProperties.class,
          MailProperties.class,
          ThrottlingProperties.class,
          TemplateProperties.class,
          YamlConfiguration.class
  })
  @EnableConfigurationProperties
  class Defaults {

    @Test
    void shouldInjectAddresses(@Autowired MailAddressesProperties properties) {
      assertThat(properties.getRecipientMail()).isEmpty();
      assertThat(properties.getSenderMail()).isEmpty();
    }

    @Test
    void shouldInjectThrottling(@Autowired ThrottlingProperties properties) {
      assertThat(properties.getClearExpiredRate()).isEqualTo(Duration.ofHours(24));
      assertThat(properties.getIp().getLimit()).isEqualTo(5);
      assertThat(properties.getIp().getWindow()).isEqualTo(Duration.ofHours(24));
      assertThat(properties.getAll().getLimit()).isEqualTo(15);
      assertThat(properties.getAll().getWindow()).isEqualTo(Duration.ofHours(24));
    }

    @Test
    void shouldConfigureTemplateResource(@Autowired TemplateProperties properties) throws IOException {
      Resource resource = properties.getResource();
      assertThat(resource.getFilename()).isEqualTo("message.mustache");
      assertThat(resource.getInputStream()).hasContent("{{content}}");
    }

    @Test
    void shouldConfigureMailStarter(@Autowired MailProperties properties) {
      assertThat(properties.getHost()).isEmpty();
      assertThat(properties.getPort()).isEqualTo(587);
      assertThat(properties.getUsername()).isEmpty();
      assertThat(properties.getPassword()).isEmpty();
      assertThat(properties.getProperties())
              .hasSize(4)
              .containsEntry("mail.smtp.auth", "true")
              .containsEntry("mail.smtp.starttls.enable", "true")
              .containsEntry("mail.smtp.starttls.required", "true")
              .containsEntry("mail.smtp.ssl.trust", "");
    }

    @Test
    void shouldSetupServerPort(@Value("${server.port}") int serverPort) {
      assertThat(serverPort).isEqualTo(80);
    }
  }

  @Nested
  @ContextConfiguration(classes = {
          MailAddressesProperties.class,
          MailProperties.class,
          ThrottlingProperties.class,
          YamlConfiguration.class
  })
  @TestPropertySource(
          properties = {
                  "MAIL_RECIPIENT_MAIL=recipient@address.com",
                  "MAIL_SENDER_MAIL=sender@address.com",
                  "MAIL_SERVER_DOMAIN=domain.com",
                  "MAIL_SERVER_PORT=123",
                  "MAIL_SERVER_USERNAME=username",
                  "MAIL_SERVER_PASSWORD=password",
                  "THROTTLING_CLEAR_EXPIRED_RATE=5h",
                  "THROTTLING_IP_LIMIT=1",
                  "THROTTLING_IP_WINDOW=2h",
                  "THROTTLING_ALL_LIMIT=3",
                  "THROTTLING_ALL_WINDOW=4h"
          })
  @EnableConfigurationProperties
  class ConfiguredByUser {

    @Test
    void shouldInjectAddresses(@Autowired MailAddressesProperties properties) {
      assertThat(properties.getRecipientMail()).isEqualTo("recipient@address.com");
      assertThat(properties.getSenderMail()).isEqualTo("sender@address.com");
    }

    @Test
    void shouldInjectThrottling(@Autowired ThrottlingProperties properties) {
      assertThat(properties.getClearExpiredRate()).isEqualTo(Duration.ofHours(5));
      assertThat(properties.getIp().getLimit()).isEqualTo(1);
      assertThat(properties.getIp().getWindow()).isEqualTo(Duration.ofHours(2));
      assertThat(properties.getAll().getLimit()).isEqualTo(3);
      assertThat(properties.getAll().getWindow()).isEqualTo(Duration.ofHours(4));
    }

    @Test
    void shouldConfigureMailStarter(@Autowired MailProperties properties) {
      assertThat(properties.getHost()).isEqualTo("domain.com");
      assertThat(properties.getPort()).isEqualTo(123);
      assertThat(properties.getUsername()).isEqualTo("username");
      assertThat(properties.getPassword()).isEqualTo("password");
      assertThat(properties.getProperties())
              .hasSize(4)
              .containsEntry("mail.smtp.auth", "true")
              .containsEntry("mail.smtp.starttls.enable", "true")
              .containsEntry("mail.smtp.starttls.required", "true")
              .containsEntry("mail.smtp.ssl.trust", "domain.com");
    }
  }

  @Configuration
  public static class YamlConfiguration {

    @Bean
    public static PropertySourcesPlaceholderConfigurer configurer() {
      PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
      YamlPropertiesFactoryBean yaml = new YamlPropertiesFactoryBean();
      yaml.setResources(new ClassPathResource("application.yml"));
      configurer.setProperties(yaml.getObject());
      return configurer;
    }
  }
}
