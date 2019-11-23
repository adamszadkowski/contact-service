package info.szadkowski.contact.controller

import info.szadkowski.contact.exception.ThrottledRequestException
import info.szadkowski.contact.model.MessageRequest
import info.szadkowski.contact.service.MessageService
import info.szadkowski.contact.template.TemplateFormatter
import info.szadkowski.contact.throttle.Throttler
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactor.mono
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping(path = ["/v1"])
class MessageController(
    private val messageService: MessageService,
    private val ipThrottler: Throttler,
    private val allThrottler: Throttler,
    private val format: TemplateFormatter
) {

    @RequestMapping(path = ["/message"], consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun sendMessage(@RequestBody message: Mono<Map<String, String>>) = mono<Unit> {
        val m = message.flatMap { msg ->
            Mono.subscriberContext()
                .map {
                    it.get<String>("ip")
                }.map {
                    if (!ipThrottler.canProcess(it) || !allThrottler.canProcess("all")) {
                        throw ThrottledRequestException()
                    }
                    msg
                }
        }.awaitFirst()
        messageService.send(
            MessageRequest(
                subject = m["subject"],
                content = m.format()
            )
        )
    }
}
