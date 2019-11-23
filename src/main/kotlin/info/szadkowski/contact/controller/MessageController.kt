package info.szadkowski.contact.controller

import info.szadkowski.contact.exception.ThrottledRequestException
import info.szadkowski.contact.model.MessageRequest
import info.szadkowski.contact.service.MessageService
import info.szadkowski.contact.template.TemplateFormatter
import info.szadkowski.contact.throttle.Throttler
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.reactor.ReactorContext
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import kotlin.coroutines.coroutineContext

@RestController
@RequestMapping(path = ["/v1"])
class MessageController(
    private val messageService: MessageService,
    private val ipThrottler: Throttler,
    private val allThrottler: Throttler,
    private val format: TemplateFormatter
) {

    @ExperimentalCoroutinesApi
    @PostMapping(path = ["/message"], consumes = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun sendMessage(@RequestBody message: Map<String, String>) {
        val ip = coroutineContext[ReactorContext]!!.context.get<String>("ip")
        if (!ipThrottler.canProcess(ip) || !allThrottler.canProcess("all")) {
            throw ThrottledRequestException()
        }
        messageService.send(
            MessageRequest(
                subject = message["subject"],
                content = message.format()
            )
        )
    }
}
