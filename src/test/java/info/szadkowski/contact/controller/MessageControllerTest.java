package info.szadkowski.contact.controller;

import info.szadkowski.contact.controller.exception.ExceptionHandlerController;
import info.szadkowski.contact.model.MessageRequest;
import info.szadkowski.contact.throttle.Throttler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class MessageControllerTest {
  private List<MessageRequest> messages;

  @Mock
  private Throttler throttler;

  private MockMvc mvc;

  @BeforeEach
  void setUp() {
    messages = new ArrayList<>();
    Mockito.when(throttler.canProcess(Mockito.anyString())).thenReturn(true);
    MessageController messageController = new MessageController(
            messages::add,
            r -> "templated:" + r.get("content"),
            throttler,
            throttler);
    mvc = MockMvcBuilders.standaloneSetup(messageController)
            .setControllerAdvice(new ExceptionHandlerController())
            .build();
  }

  @Test
  void shouldSendCorrectMailRequest() throws Exception {
    mvc.perform(post("/v1/message")
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .content("" +
                    "{" +
                    "\"subject\": \"mySubject\"," +
                    "\"content\": \"myContent\"" +
                    "}")
    ).andExpect(status().isOk());

    assertThat(messages).containsExactly(MessageRequest.builder()
            .subject("mySubject")
            .content("templated:myContent")
            .build());
  }
}
