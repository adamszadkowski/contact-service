package info.szadkowski.contact.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import info.szadkowski.contact.model.MessageRequest;
import info.szadkowski.contact.properties.MailAddressesProperties;
import info.szadkowski.contact.model.MessageContent;
import info.szadkowski.contact.throttle.Throttler;
import lombok.Getter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MessageControllerTest {
  private static final ObjectMapper MAPPER = new ObjectMapper();

  private List<MessageContent> mails;
  private MailAddressesProperties mailAddressesProperties;
  private MockedThrottler ipThrottler;
  private MockedThrottler allThrottler;
  private MockMvc mvc;

  @BeforeEach
  void setUp() {
    mails = new ArrayList<>();
    mailAddressesProperties = new MailAddressesProperties();
    ipThrottler = new MockedThrottler();
    allThrottler = new MockedThrottler();
    MessageController messageController = new MessageController(
            mails::add,
            ipThrottler,
            allThrottler);
    mvc = MockMvcBuilders.standaloneSetup(messageController)
            .build();
  }

  @Test
  void shouldSendCorrectMailRequest() throws Exception {
    mailAddressesProperties.setRecipientMail("recipient@address.com");
    mailAddressesProperties.setSenderMail("sender@address.com");
    mvc.perform(
            post("/v1/message")
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .content(serialize(MessageRequest.builder()
                            .subject("mySubject")
                            .content("myContent")
                            .build()))
    ).andExpect(status().isOk());

    assertThat(mails).containsExactly(MessageContent.builder()
            .subject("mySubject")
            .content("myContent")
            .build());
  }

  @Nested
  class Throttling {

    @Test
    void shouldNotSendWhenThrottlingLimitHasBeenReached() throws Exception {
      ipThrottler.setCannotProcess("10.0.0.1");

      mvc.perform(
              post("/v1/message")
                      .with(remoteAddr("10.0.0.1"))
                      .contentType(MediaType.APPLICATION_JSON_UTF8)
                      .content(serialize(MessageRequest.builder()
                              .subject("mySubject")
                              .content("myContent")
                              .build()))
      ).andExpect(status().isTooManyRequests());
      assertThat(mails).isEmpty();
    }

    @Test
    void shouldSendWhenLimitHasBeenReachedForOtherIp() throws Exception {
      ipThrottler.setCannotProcess("10.0.0.1");

      mvc.perform(
              post("/v1/message")
                      .with(remoteAddr("10.0.0.2"))
                      .contentType(MediaType.APPLICATION_JSON_UTF8)
                      .content(serialize(MessageRequest.builder()
                              .subject("mySubject")
                              .content("myContent")
                              .build()))
      ).andExpect(status().isOk());
      assertThat(mails).hasSize(1);
    }

    @Test
    void shouldThrottleIndependentFromIp() throws Exception {
      allThrottler.setCannotProcess("all");

      mvc.perform(
              post("/v1/message")
                      .with(remoteAddr("10.0.0.1"))
                      .contentType(MediaType.APPLICATION_JSON_UTF8)
                      .content(serialize(MessageRequest.builder()
                              .subject("mySubject")
                              .content("myContent")
                              .build()))
      ).andExpect(status().isTooManyRequests());
      assertThat(mails).isEmpty();
    }

    @Test
    void shouldCheckIpThrottlerFirstAndFailFast() throws Exception {
      ipThrottler.setCannotProcess("10.0.0.1");

      mvc.perform(
              post("/v1/message")
                      .with(remoteAddr("10.0.0.1"))
                      .contentType(MediaType.APPLICATION_JSON_UTF8)
                      .content(serialize(MessageRequest.builder()
                              .subject("mySubject")
                              .content("myContent")
                              .build()))
      ).andExpect(status().isTooManyRequests());

      assertThat(ipThrottler.getCanProcessCounter()).isEqualTo(1);
      assertThat(allThrottler.getCanProcessCounter()).isEqualTo(0);
    }

    private RequestPostProcessor remoteAddr(String remoteAddr) {
      return request -> {
        request.setRemoteAddr(remoteAddr);
        return request;
      };
    }
  }

  private static String serialize(Object v) throws JsonProcessingException {
    return MAPPER.writeValueAsString(v);
  }

  private static class MockedThrottler implements Throttler {
    private Map<String, Boolean> allow = new HashMap<>();

    @Getter
    private int canProcessCounter = 0;

    @Override
    public boolean canProcess(String key) {
      canProcessCounter++;
      return allow.getOrDefault(key, true);
    }

    private void setCannotProcess(String key) {
      allow.put(key, false);
    }

    @Override
    public void clearExpired() {
    }
  }
}
