package info.szadkowski.contact;

import info.szadkowski.contact.configuration.MainConfiguration;
import info.szadkowski.contact.controller.MessageController;
import info.szadkowski.contact.properties.MailAddressesProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackageClasses = {
        MainConfiguration.class,
        MessageController.class,
        MailAddressesProperties.class
})
public class ContactServiceApplication {
  public static void main(String[] args) {
    SpringApplication.run(ContactServiceApplication.class, args);
  }
}
