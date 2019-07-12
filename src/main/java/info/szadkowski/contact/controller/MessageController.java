package info.szadkowski.contact.controller;

import info.szadkowski.contact.exception.ThrottledRequestException;
import info.szadkowski.contact.model.MessageRequest;
import info.szadkowski.contact.service.MessageService;
import info.szadkowski.contact.template.TemplateFormatter;
import info.szadkowski.contact.throttle.Throttler;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping(path = "/v1")
public class MessageController {
  private final MessageService messageService;
  private final TemplateFormatter formatter;
  private final Throttler ipThrottler;
  private final Throttler allThrottler;

  public MessageController(MessageService messageService,
                           TemplateFormatter formatter,
                           Throttler ipThrottler,
                           Throttler allThrottler) {
    this.messageService = messageService;
    this.formatter = formatter;
    this.ipThrottler = ipThrottler;
    this.allThrottler = allThrottler;
  }

  @RequestMapping(path = "/message", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public void sendMessage(@RequestBody Map<String, String> message,
                          HttpServletRequest request) {
    if (!ipThrottler.canProcess(request.getRemoteAddr()) || !allThrottler.canProcess("all")) {
      throw new ThrottledRequestException();
    }

    messageService.send(MessageRequest.builder()
            .subject(message.get("subject"))
            .content(formatter.format(message))
            .build());
  }
}
