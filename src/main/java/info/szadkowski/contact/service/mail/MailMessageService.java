package info.szadkowski.contact.service.mail;

import info.szadkowski.contact.model.MessageRequest;
import info.szadkowski.contact.properties.MailAddressesProperties;
import info.szadkowski.contact.service.MessageService;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.internet.MimeMessage;

public class MailMessageService implements MessageService {
  private final MailAddressesProperties mailAddressesProperties;
  private final JavaMailSender javaMailSender;

  public MailMessageService(MailAddressesProperties mailAddressesProperties,
                            JavaMailSender javaMailSender) {
    this.mailAddressesProperties = mailAddressesProperties;
    this.javaMailSender = javaMailSender;
  }

  @Override
  public void send(MessageRequest message) {
    MimeMessage mimeMessage = javaMailSender.createMimeMessage();

    MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
    try {
      helper.setSubject(message.getSubject());
      helper.setFrom(mailAddressesProperties.getSenderMail());
      helper.setTo(mailAddressesProperties.getRecipientMail());
      helper.setText(message.getContent());

      javaMailSender.send(mimeMessage);
    } catch (Exception e) {
      throw new MessageSendException(e);
    }
  }
}
