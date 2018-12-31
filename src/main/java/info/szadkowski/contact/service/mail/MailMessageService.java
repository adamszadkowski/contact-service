package info.szadkowski.contact.service.mail;

import info.szadkowski.contact.model.MessageContent;
import info.szadkowski.contact.properties.MailAddressesProperties;
import info.szadkowski.contact.service.MessageService;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

public class MailMessageService implements MessageService {
  private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(MailMessageService.class);

  private final MailAddressesProperties mailAddressesProperties;
  private final JavaMailSender javaMailSender;

  public MailMessageService(MailAddressesProperties mailAddressesProperties,
                            JavaMailSender javaMailSender) {
    this.mailAddressesProperties = mailAddressesProperties;
    this.javaMailSender = javaMailSender;
  }

  @Override
  public void send(MessageContent content) {
    MimeMessage mimeMessage = javaMailSender.createMimeMessage();

    MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
    try {
      helper.setSubject(content.getSubject());
      helper.setFrom(mailAddressesProperties.getSenderMail());
      helper.setTo(mailAddressesProperties.getRecipientMail());
      helper.setText(content.getContent());
    } catch (MessagingException e) {
      LOG.error("Cannot create a MimeMessage", e);
    }

    javaMailSender.send(mimeMessage);
  }
}
