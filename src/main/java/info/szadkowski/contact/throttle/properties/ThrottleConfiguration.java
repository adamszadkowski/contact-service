package info.szadkowski.contact.throttle.properties;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ThrottleConfiguration {
  private long limit;
}
