package info.szadkowski.contact.properties;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = MailAddressesProperties.class)
@TestPropertySource(properties = {
        "mail.recipientMail=recipient@address.com",
        "mail.senderMail=sender@address.com"
})
@EnableConfigurationProperties
class MailAddressesPropertiesTest {

  @Test
  void shouldInjectRecipientMail(@Autowired MailAddressesProperties properties) {
    assertThat(properties).isNotNull();
    assertThat(properties.getRecipientMail()).isEqualTo("recipient@address.com");
    assertThat(properties.getSenderMail()).isEqualTo("sender@address.com");
  }
}
