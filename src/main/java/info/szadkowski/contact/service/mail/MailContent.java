package info.szadkowski.contact.service.mail;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public final class MailContent {
  private String subject;
  private String sender;
  private String recipient;
  private String content;
}
