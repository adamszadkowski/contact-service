package info.szadkowski.contact.service.mail;

import info.szadkowski.contact.properties.MailAddressesProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@ExtendWith(MockitoExtension.class)
class SpringMailSenderServiceTest {
  private MailAddressesProperties mailAddressesProperties;
  private SpringJavaMailSenderService service;

  @Mock
  private JavaMailSender sender;

  @BeforeEach
  void setUp() {
    mailAddressesProperties = new MailAddressesProperties();
    service = new SpringJavaMailSenderService(mailAddressesProperties, sender);
  }

  @Test
  void shouldSendFormattedMail(@Mock MimeMessage mimeMessage) throws Exception {
    Mockito.when(sender.createMimeMessage()).thenReturn(mimeMessage);
    mailAddressesProperties.setSenderMail("sender@address.com");
    mailAddressesProperties.setRecipientMail("recipient@address.com");

    service.send(MailContent.builder()
            .subject("subject")
            .content("content")
            .build());

    Mockito.verify(mimeMessage).setSubject("subject");
    Mockito.verify(mimeMessage).setFrom(new InternetAddress("sender@address.com"));
    Mockito.verify(mimeMessage).setRecipient(Message.RecipientType.TO, new InternetAddress("recipient@address.com"));
    Mockito.verify(mimeMessage).setText("content");

    Mockito.verify(sender).send(mimeMessage);
  }
}
