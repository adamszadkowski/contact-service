package info.szadkowski.contact.controller

import info.szadkowski.contact.controller.exception.ExceptionHandlerController
import info.szadkowski.contact.model.MessageRequest
import info.szadkowski.contact.service.MessageService
import info.szadkowski.contact.throttle.Throttler
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import strikt.api.expectThat
import strikt.assertions.containsExactly

class MessageControllerTest : MessageService {
    lateinit var messageService: (MessageRequest) -> Unit
    lateinit var client: WebTestClient

    override suspend fun send(message: MessageRequest) = messageService(message)

    @BeforeEach
    fun setUp() {
        val dummyThrottler = object : Throttler {
            override fun canProcess(key: String) = true
            override fun clearExpired() = Unit
        }
        val messageController = MessageController(this, dummyThrottler, dummyThrottler) {
            "templated:" + this["content"]
        }
        client = WebTestClient
            .bindToController(messageController)
            .controllerAdvice(ExceptionHandlerController())
            .webFilter<WebTestClient.ControllerSpec>(IpReadingFilter())
            .build()
    }

    @Test
    fun `Should send correct mail request`() {
        val messages = mutableListOf<MessageRequest>()
        messageService = { messages.add(it) }

        client.post()
            .uri("/v1/message")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(
                """
                    {
                        "subject": "mySubject",
                        "content": "myContent"
                    }
                """.trimIndent()
            )
            .exchange()
            .expectStatus().isOk

        expectThat(messages).containsExactly(
            MessageRequest(
                subject = "mySubject",
                content = "templated:myContent"
            )
        )
    }

    @Test
    fun `Should fail on incorrect mail request`() {
        messageService = { throw MessageService.MessageSendException() }

        client.post()
            .uri("/v1/message")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue("{}")
            .exchange()
            .expectStatus().isBadRequest
    }
}
