package info.szadkowski.contact.controller

import info.szadkowski.contact.model.MessageRequest
import info.szadkowski.contact.service.MessageService
import info.szadkowski.contact.template.TemplateFormatter
import info.szadkowski.contact.throttle.spring.aspect.Throttle
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(path = ["/v1"])
class MessageController(
    private val messageService: MessageService,
    private val formatter: TemplateFormatter
) {

    @Throttle
    @RequestMapping(path = ["/message"], consumes = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    fun sendMessage(@RequestBody message: Map<String, String>) {
        messageService.send(
            MessageRequest(
                subject = message["subject"],
                content = formatter(message)
            )
        )
    }
}
