package info.szadkowski.contact.controller

import info.szadkowski.contact.controller.exception.ExceptionHandlerController
import info.szadkowski.contact.model.MessageRequest
import info.szadkowski.contact.service.MessageService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient

class MessageControllerTest : MessageService {
    lateinit var messageService: (MessageRequest) -> Unit
    lateinit var client: WebTestClient

    override fun send(message: MessageRequest) = messageService(message)

    @BeforeEach
    fun setUp() {
        val messageController = MessageController(this) {
            "templated:" + this["content"]
        }
        client = WebTestClient
            .bindToController(messageController)
            .controllerAdvice(ExceptionHandlerController())
            .build()
    }

    @Test
    fun `Should send correct mail request`() {
        val messages = mutableListOf<MessageRequest>()
        messageService = { messages.add(it) }

        client.post()
            .uri("/v1/message")
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .syncBody(
                """
                    {
                        "subject": "mySubject",
                        "content": "myContent"
                    }
                """.trimIndent()
            )
            .exchange()
            .expectStatus().isOk

        assertThat(messages).containsExactly(
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
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .syncBody("{}")
            .exchange()
            .expectStatus().isBadRequest
    }
}
