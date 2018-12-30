package info.szadkowski.contact.controller;

import info.szadkowski.contact.properties.MailAddressesProperties;
import info.szadkowski.contact.service.mail.MailContent;
import info.szadkowski.contact.service.mail.MailSenderService;
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
  private final MailSenderService mailSenderService;
  private final MailAddressesProperties mailAddressesProperties;
  private final Throttler ipThrottler;
  private final Throttler allThrottler;

  public MessageController(MailSenderService mailSenderService,
                           MailAddressesProperties mailAddressesProperties,
                           Throttler ipThrottler,
                           Throttler allThrottler) {
    this.mailSenderService = mailSenderService;
    this.mailAddressesProperties = mailAddressesProperties;
    this.ipThrottler = ipThrottler;
    this.allThrottler = allThrottler;
  }

  @RequestMapping(path = "/message", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<Void> sendMail(@RequestBody Map<String, String> message,
                                       HttpServletRequest request) {
    if (ipThrottler.canProcess(request.getRemoteAddr()) && allThrottler.canProcess("all")) {
      mailSenderService.send(MailContent.builder()
              .subject(message.get("subject"))
              .sender(mailAddressesProperties.getSenderMail())
              .recipient(mailAddressesProperties.getRecipientMail())
              .content(message.get("content"))
              .build());

      return new ResponseEntity<>(HttpStatus.OK);
    }
    return new ResponseEntity<>(HttpStatus.TOO_MANY_REQUESTS);
  }
}
