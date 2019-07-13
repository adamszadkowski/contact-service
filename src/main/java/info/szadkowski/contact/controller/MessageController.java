package info.szadkowski.contact.controller;

import info.szadkowski.contact.throttle.spring.aspect.Throttle;
import info.szadkowski.contact.model.MessageRequest;
import info.szadkowski.contact.service.MessageService;
import info.szadkowski.contact.template.TemplateFormatter;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping(path = "/v1")
public class MessageController {
  private final MessageService messageService;
  private final TemplateFormatter formatter;

  public MessageController(MessageService messageService,
                           TemplateFormatter formatter) {
    this.messageService = messageService;
    this.formatter = formatter;
  }

  @Throttle
  @RequestMapping(path = "/message", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public void sendMessage(@RequestBody Map<String, String> message) {
    messageService.send(MessageRequest.builder()
            .subject(message.get("subject"))
            .content(formatter.format(message))
            .build());
  }
}
