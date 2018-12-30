package info.szadkowski.contact.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "mail")
@Data
public class MailAddressesProperties {
  private String recipientMail;
  private String senderMail;
}
