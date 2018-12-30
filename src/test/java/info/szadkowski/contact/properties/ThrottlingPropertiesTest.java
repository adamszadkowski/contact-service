package info.szadkowski.contact.properties;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = ThrottlingProperties.class)
@TestPropertySource(properties = {
        "throttling.clearExpiredRate=5s",
        "throttling.ip.limit=1",
        "throttling.ip.window=5s",
        "throttling.all.limit=2",
        "throttling.all.window=10s",
})
@EnableConfigurationProperties
class ThrottlingPropertiesTest {

  @Test
  void shouldInjectRecipientMail(@Autowired ThrottlingProperties properties) {
    assertThat(properties).isNotNull();
    assertThat(properties.getClearExpiredRate()).isEqualTo(Duration.ofSeconds(5));
    assertThat(properties.getIp().getLimit()).isEqualTo(1);
    assertThat(properties.getIp().getWindow()).isEqualTo(Duration.ofSeconds(5));
    assertThat(properties.getAll().getLimit()).isEqualTo(2);
    assertThat(properties.getAll().getWindow()).isEqualTo(Duration.ofSeconds(10));
  }
}
