package info.szadkowski.contact;

import info.szadkowski.contact.controller.MessageController;
import info.szadkowski.contact.controller.exception.ExceptionHandlerController;
import info.szadkowski.contact.properties.MailAddressesProperties;
import info.szadkowski.contact.properties.TemplateProperties;
import info.szadkowski.contact.properties.ThrottlingProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class ContactServiceApplicationTest {

  @Autowired
  private MessageController controller;

  @Autowired
  private ExceptionHandlerController exceptionController;

  @Autowired
  private MailAddressesProperties mailAddressesProperties;

  @Autowired
  private ThrottlingProperties throttlingProperties;

  @Autowired
  private TemplateProperties templateProperties;

  @Test
  void sanityCheck() {
    assertThat(controller).isNotNull();
    assertThat(exceptionController).isNotNull();
    assertThat(mailAddressesProperties).isNotNull();
    assertThat(throttlingProperties).isNotNull();
    assertThat(templateProperties).isNotNull();
  }
}
