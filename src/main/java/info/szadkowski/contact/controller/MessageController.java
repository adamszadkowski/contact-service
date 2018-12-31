package info.szadkowski.contact.controller;

import info.szadkowski.contact.model.MessageRequest;
import info.szadkowski.contact.service.MessageService;
import info.szadkowski.contact.throttle.Throttler;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping(path = "/v1")
public class MessageController {
  private final MessageService messageService;
  private final Throttler ipThrottler;
  private final Throttler allThrottler;

  public MessageController(MessageService messageService,
                           Throttler ipThrottler,
                           Throttler allThrottler) {
    this.messageService = messageService;
    this.ipThrottler = ipThrottler;
    this.allThrottler = allThrottler;
  }

  @RequestMapping(path = "/message", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<Void> sendMessage(@RequestBody Map<String, String> message,
                                          HttpServletRequest request) {
    if (ipThrottler.canProcess(request.getRemoteAddr()) && allThrottler.canProcess("all")) {
      messageService.send(MessageRequest.builder()
              .subject(message.get("subject"))
              .content(message.get("content"))
              .build());

      return new ResponseEntity<>(HttpStatus.OK);
    }
    return new ResponseEntity<>(HttpStatus.TOO_MANY_REQUESTS);
  }
}
