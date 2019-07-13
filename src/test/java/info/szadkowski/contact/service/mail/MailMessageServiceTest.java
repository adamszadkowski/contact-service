package info.szadkowski.contact.service.mail;

import info.szadkowski.contact.model.MessageRequest;
import info.szadkowski.contact.properties.MailAddressesProperties;
import info.szadkowski.contact.service.MessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;

import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class MailMessageServiceTest {
  private MailMessageService service;

  @Mock
  private JavaMailSender sender;

  @Mock
  private MimeMessage mimeMessage;

  @BeforeEach
  void setUp() {
    MailAddressesProperties mailAddressesProperties = new MailAddressesProperties();
    mailAddressesProperties.setSenderMail("sender@address.com");
    mailAddressesProperties.setRecipientMail("recipient@address.com");
    Mockito.when(sender.createMimeMessage()).thenReturn(mimeMessage);
    service = new MailMessageService(mailAddressesProperties, sender);
  }

  @Test
  void shouldSendFormattedMail() throws Exception {
    service.send(MessageRequest.builder()
            .subject("subject")
            .content("content")
            .build());

    Mockito.verify(mimeMessage).setSubject("subject");
    Mockito.verify(mimeMessage).setFrom(new InternetAddress("sender@address.com"));
    Mockito.verify(mimeMessage).setRecipient(Message.RecipientType.TO, new InternetAddress("recipient@address.com"));
    Mockito.verify(mimeMessage).setText("content");

    Mockito.verify(sender).send(mimeMessage);
  }

  @Test
  void shouldWrapMailException(@Mock MailException mailException) {
    Mockito.doThrow(mailException).when(sender).send(Mockito.any(MimeMessage.class));
    MessageRequest request = MessageRequest.builder()
            .subject("subject")
            .content("content")
            .build();

    assertThatThrownBy(() -> service.send(request))
            .isExactlyInstanceOf(MessageService.MessageSendException.class)
            .hasCause(mailException);
  }

  @Test
  void shouldThrowOnInvalidRequest() {
    MessageRequest request = MessageRequest.builder().build();

    assertThatThrownBy(() -> service.send(request))
            .isExactlyInstanceOf(MessageService.MessageSendException.class)
            .hasMessageContaining("Subject must not be null");
  }
}
