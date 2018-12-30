package info.szadkowski.contact;

import info.szadkowski.contact.controller.MessageController;
import info.szadkowski.contact.properties.MailAddressesProperties;
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
  private MailAddressesProperties mailAddressesProperties;

  @Autowired
  private ThrottlingProperties throttlingProperties;

  @Test
  void sanityCheck() {
    assertThat(controller).isNotNull();
    assertThat(mailAddressesProperties).isNotNull();
    assertThat(throttlingProperties).isNotNull();
  }
}
