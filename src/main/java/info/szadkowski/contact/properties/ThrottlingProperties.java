package info.szadkowski.contact.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@ConfigurationProperties(prefix = "throttling")
@Data
public class ThrottlingProperties {
  private Duration clearExpiredRate;
  private ThrottlingScope ip;
  private ThrottlingScope all;

  @Data
  public static class ThrottlingScope {
    private long limit;
    private Duration window;
  }
}
