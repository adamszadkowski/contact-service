package info.szadkowski.contact.service.mail;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

public class SpringJavaMailSenderService implements MailSenderService {
  private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SpringJavaMailSenderService.class);

  private final JavaMailSender javaMailSender;

  public SpringJavaMailSenderService(JavaMailSender javaMailSender) {
    this.javaMailSender = javaMailSender;
  }

  @Override
  public void send(MailContent content) {
    MimeMessage mimeMessage = javaMailSender.createMimeMessage();

    MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
    try {
      helper.setSubject(content.getSubject());
      helper.setFrom(content.getSender());
      helper.setTo(content.getRecipient());
      helper.setText(content.getContent());
    } catch (MessagingException e) {
      LOG.error("Cannot create a MimeMessage", e);
    }

    javaMailSender.send(mimeMessage);
  }
}
