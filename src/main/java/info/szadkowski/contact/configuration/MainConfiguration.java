package info.szadkowski.contact.configuration;

import info.szadkowski.contact.service.mail.MailSenderService;
import info.szadkowski.contact.service.mail.SpringJavaMailSenderService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;

@Configuration
public class MainConfiguration {

  @Bean
  public MailSenderService mailSenderService(JavaMailSender javaMailSender) {
    return new SpringJavaMailSenderService(javaMailSender);
  }
}
