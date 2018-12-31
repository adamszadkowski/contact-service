package info.szadkowski.contact.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public final class MessageContent {
  private String subject;
  private String content;
}
