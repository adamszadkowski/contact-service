package info.szadkowski.contact.controller;

import info.szadkowski.contact.controller.exception.ExceptionHandlerController;
import info.szadkowski.contact.model.MessageRequest;
import info.szadkowski.contact.service.MessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
class MessageControllerTest implements MessageService {
  private MessageService messageService;
  private List<MessageRequest> messages;
  private MockMvc mvc;

  @BeforeEach
  void setUp() {
    messages = new ArrayList<>();
    MessageController messageController = new MessageController(
            this,
            r -> "templated:" + r.get("content")
    );
    mvc = MockMvcBuilders.standaloneSetup(messageController)
            .setControllerAdvice(new ExceptionHandlerController())
            .build();
  }

  @Override
  public void send(MessageRequest content) {
    messageService.send(content);
  }

  @Test
  void shouldSendCorrectMailRequest() throws Exception {
    messageService = messages::add;

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

  @Test
  void shouldFailOnIncorrectMailRequest() throws Exception {
    messageService = r -> {
      throw new MessageSendException();
    };

    mvc.perform(post("/v1/message")
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .content("{}")
    ).andExpect(status().isBadRequest());
  }
}
