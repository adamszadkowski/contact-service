package info.szadkowski.contact.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@JsonDeserialize(builder = MessageRequest.MessageRequestBuilder.class)
public class MessageRequest {
  private String subject;
  private String content;

  @JsonPOJOBuilder(withPrefix = "")
  public static class MessageRequestBuilder {
  }
}
