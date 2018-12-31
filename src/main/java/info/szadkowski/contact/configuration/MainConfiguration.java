package info.szadkowski.contact.configuration;

import info.szadkowski.contact.properties.MailAddressesProperties;
import info.szadkowski.contact.service.MessageService;
import info.szadkowski.contact.service.mail.MailMessageService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;

@Configuration
public class MainConfiguration {

  @Bean
  public MessageService mailSenderService(MailAddressesProperties mailAddressesProperties,
                                          JavaMailSender javaMailSender) {
    return new MailMessageService(mailAddressesProperties, javaMailSender);
  }
}
